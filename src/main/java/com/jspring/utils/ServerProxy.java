package com.jspring.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jspring.Encodings;
import com.jspring.Exceptions;
import com.jspring.Processes;
import com.jspring.Strings;
import com.jspring.io.Files;
import com.jspring.utils.ServerManager.ServerConf;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.HTTPProxyData;
import ch.ethz.ssh2.SCPClient;
import ch.ethz.ssh2.SFTPv3Client;
import ch.ethz.ssh2.SFTPv3FileAttributes;
import ch.ethz.ssh2.SFTPv3FileHandle;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;

public final class ServerProxy {

	private static final Logger log = LoggerFactory.getLogger(ServerProxy.class);

	private final Connection conn;
	private final ServerConf _config;

	public ServerConf getConfig() {
		return _config;
	}

	private ServerProxy(ServerConf config, Connection conn) {
		this._config = config;
		this.conn = conn;
	}

	public static ServerProxy newInstance(ServerConf config) {
		try {
			if (Strings.isNullOrEmpty(config.host)) {
				throw Exceptions.newNullArgumentException("config.host");
			}
			Connection conn;
			if (config.port > 0) {
				conn = new Connection(config.host, config.port);
			} else {
				conn = new Connection(config.host);
			}
			if (!Strings.isNullOrEmpty(config.proxyHost)) {
				conn.setProxyData(new HTTPProxyData(config.proxyHost, config.proxyPort));
			}
			conn.connect();
			if (!Strings.isNullOrEmpty(config.keyFile)) {
				if (!conn.authenticateWithPublicKey(config.user, new File(config.keyFile), config.keyFilePassword)) {
					throw Exceptions.newInstance("Authentication failed.");
				}
			} else if (!conn.authenticateWithPassword(config.user, config.password)) {
				throw Exceptions.newInstance("Authentication failed.");
			}
			// Correct
			if (!Strings.isNullOrEmpty(config.localTempPath)) {
				if (config.localTempPath.indexOf('\\') >= 0) {
					config.localTempPath = config.localTempPath.replace('\\', '/');
					if (config.localTempPath.charAt(config.localTempPath.length() - 1) != '/') {
						config.localTempPath += '/';
					}
				}
			}
			if (!Strings.isNullOrEmpty(config.remoteTempPath)) {
				if (config.remoteTempPath.indexOf('\\') >= 0) {
					config.remoteTempPath = config.remoteTempPath.replace('\\', '/');
					if (config.remoteTempPath.charAt(config.remoteTempPath.length() - 1) != '/') {
						config.remoteTempPath += '/';
					}
				}
			}
			//
			return new ServerProxy(config, conn);
		} catch (Exception e) {
			throw Exceptions.newInstance(e);
		}
	}

	public void tryClose() {
		if (null != _sftpv3Client) {
			try {
				_sftpv3Client.close();
			} catch (Exception e) {
			}
		}
		try {
			conn.close();
		} catch (Exception e) {
		}
	}

	// ///////////////////////////////////////////
	// Send File
	// ///////////////////////////////////////////
	private SCPClient _scpClient;

	private SCPClient getSCPClient() {
		if (null == _scpClient) {
			_scpClient = new SCPClient(conn);
		}
		return _scpClient;
	}

	public void executeRemote(String cmd) {
		Session sess = null;
		//
		InputStream stdout = null;
		BufferedReader stdoutReader = null;
		//
		InputStream stderr = null;
		BufferedReader stderrReader = null;
		try {
			log.debug("Execute remote: " + cmd);
			sess = conn.openSession();
			sess.execCommand(cmd);
			//
			stdout = new StreamGobbler(sess.getStdout());
			stdoutReader = new BufferedReader(new InputStreamReader(stdout, com.jspring.Encodings.UTF8.value));
			//
			stderr = new StreamGobbler(sess.getStderr());
			stderrReader = new BufferedReader(new InputStreamReader(stderr, com.jspring.Encodings.UTF8.value));
			//
			String line;
			while (null != (line = stdoutReader.readLine())) {
				log.debug(line);
			}
			while (null != (line = stderrReader.readLine())) {
				log.debug(line);
			}
		} catch (Exception e) {
			throw Exceptions.newInstance(e);
		} finally {
			if (null != stderrReader) {
				try {
					stderrReader.close();
				} catch (Exception e) {
				}
			}
			if (null != stderr) {
				try {
					stderr.close();
				} catch (Exception e) {
				}
			}
			if (null != stdoutReader) {
				try {
					stdoutReader.close();
				} catch (Exception e) {
				}
			}
			if (null != stdout) {
				try {
					stdout.close();
				} catch (Exception e) {
				}
			}
			if (null != sess) {
				try {
					sess.close();
				} catch (Exception e) {
				}
			}
		}
	}

	// ///////////////////////////////////////////
	// Upload
	// ///////////////////////////////////////////
	public static class UploadArgs {
		public String localSourceFilename;
		public String remoteTargetPathOrFilename;
		//
		public boolean compressBeforeUpload = true;
		public boolean decompressAfterUpload = true;
	}

	public static class UploadResult {
		public boolean isSuccess;
		public String errorMessage;
		//
		public String remoteTargetFilename;
	}

	public UploadResult upload(UploadArgs args) {
		// Check
		if (null == args) {
			throw Exceptions.newNullArgumentException("args");
		}
		if (Strings.isNullOrEmpty(args.localSourceFilename)) {
			throw Exceptions.newNullArgumentException("sourceLocalFilename");
		}
		if (!Files.isExist(args.localSourceFilename)) {
			throw Exceptions.newInstance("Source local filename not exist!");
		}
		if (Strings.isNullOrEmpty(args.remoteTargetPathOrFilename)) {
			args.remoteTargetPathOrFilename = getConfig().remoteTempPath;
		}
		// Correct 1
		if (args.localSourceFilename.indexOf('\\') >= 0) {
			args.localSourceFilename = args.localSourceFilename.replace('\\', '/');
		}
		String sourceLocalShortFilename = args.localSourceFilename
				.substring(args.localSourceFilename.lastIndexOf('/') + 1);
		// Correct 2
		if (args.remoteTargetPathOrFilename.indexOf('\\') >= 0) {
			args.remoteTargetPathOrFilename = args.remoteTargetPathOrFilename.replace('\\', '/');
		}
		boolean isTargetRemotePath = args.remoteTargetPathOrFilename
				.charAt(args.remoteTargetPathOrFilename.length() - 1) == '/';
		String targetRemotePath = isTargetRemotePath ? args.remoteTargetPathOrFilename
				: args.remoteTargetPathOrFilename.substring(0, args.remoteTargetPathOrFilename.lastIndexOf('/') + 1);
		//
		UploadResult result = new UploadResult();
		//
		if (args.compressBeforeUpload && !args.localSourceFilename.endsWith(".bz2")) {
			String tempFilename;
			if (Strings.isNullOrEmpty(getConfig().localTempPath)) {
				Processes.execute("bzip2 -z -k " + args.localSourceFilename);
				//
				tempFilename = args.localSourceFilename + ".bz2";
				sourceLocalShortFilename += ".bz2";
			} else {
				//
				tempFilename = getConfig().localTempPath + sourceLocalShortFilename;
				Processes.execute("cp " + args.localSourceFilename + " " + tempFilename);
				Processes.execute("bzip2 -z " + tempFilename);
				//
				tempFilename += ".bz2";
				sourceLocalShortFilename += ".bz2";
			}
			log.debug(String.format("SCP: [local]%s -> [remote]%s", tempFilename, targetRemotePath));
			try {
				getSCPClient().put(tempFilename, targetRemotePath);
			} catch (Exception e) {
				result.isSuccess = false;
				result.errorMessage = "SCP failed: " + e.getClass().getSimpleName() + "," + e.getMessage();
				return result;
			} finally {
				Files.delete(tempFilename);
			}
		} else {
			log.debug(String.format("SCP: [local]%s -> [remote]%s", args.localSourceFilename, targetRemotePath));
			try {
				getSCPClient().put(args.localSourceFilename, targetRemotePath);
			} catch (Exception e) {
				result.isSuccess = false;
				result.errorMessage = "SCP failed: " + e.getClass().getSimpleName() + "," + e.getMessage();
				return result;
			}
		}
		result.isSuccess = true;
		if (isTargetRemotePath) {
			result.remoteTargetFilename = targetRemotePath + sourceLocalShortFilename;
		} else {
			executeRemote("mv " + targetRemotePath + sourceLocalShortFilename + " " + args.remoteTargetPathOrFilename);
			result.remoteTargetFilename = args.remoteTargetPathOrFilename;
		}
		//
		if (args.decompressAfterUpload) {
			executeRemote("bzip2 -d " + result.remoteTargetFilename);
			result.remoteTargetFilename = result.remoteTargetFilename.substring(0,
					result.remoteTargetFilename.lastIndexOf('.'));
		}
		return result;
	}

	// ///////////////////////////////////////////
	// Download
	// ///////////////////////////////////////////
	public static class DownloadArgs {
		public String remoteSourceFilename;
		public String localTargetPathOrFilename;
		//
		public boolean compressBeforeDownload = true;
		public boolean decompressAfterDownload = true;
	}

	public static class DownloadResult {
		public boolean isSuccess;
		public String errorMessage;
		//
		public String localTargetFilename;
	}

	public DownloadResult download(DownloadArgs args) {
		// Check
		if (null == args) {
			throw Exceptions.newNullArgumentException("args");
		}
		if (Strings.isNullOrEmpty(args.remoteSourceFilename)) {
			throw Exceptions.newNullArgumentException("sourceRemoteFilename");
		}
		if (Strings.isNullOrEmpty(args.localTargetPathOrFilename)) {
			throw Exceptions.newNullArgumentException("targetLocalPathOrFilename");
		}
		// Correct 1
		if (args.remoteSourceFilename.indexOf('\\') >= 0) {
			args.remoteSourceFilename = args.remoteSourceFilename.replace('\\', '/');
		}
		String sourceRemoteShortFilename = args.remoteSourceFilename
				.substring(args.remoteSourceFilename.lastIndexOf('/') + 1);
		// Correct 2
		if (args.localTargetPathOrFilename.indexOf('\\') >= 0) {
			args.localTargetPathOrFilename = args.localTargetPathOrFilename.replace('\\', '/');
		}
		boolean isTargetLocalPath = args.localTargetPathOrFilename
				.charAt(args.localTargetPathOrFilename.length() - 1) == '/';
		String targetLocalPath = isTargetLocalPath ? args.localTargetPathOrFilename
				: args.localTargetPathOrFilename.substring(0, args.localTargetPathOrFilename.lastIndexOf('/') + 1);
		//
		DownloadResult result = new DownloadResult();
		if (args.compressBeforeDownload && !args.remoteSourceFilename.endsWith(".bz2")) {
			sourceRemoteShortFilename += ".bz2";
			String tempFilename;
			if (Strings.isNullOrEmpty(getConfig().remoteTempPath)) {
				tempFilename = args.remoteSourceFilename + ".bz2";
			} else {
				tempFilename = getConfig().remoteTempPath + sourceRemoteShortFilename;
			}
			executeRemote("bzip2 -9 -c " + args.remoteSourceFilename + " > " + tempFilename);
			log.debug(String.format("SCP: [remote]%s -> [local]%s", tempFilename, targetLocalPath));
			try {
				getSCPClient().get(tempFilename, targetLocalPath);
			} catch (Exception e) {
				result.isSuccess = false;
				result.errorMessage = "SCP failed: " + e.getClass().getSimpleName() + "," + e.getMessage();
				return result;
			} finally {
				executeRemote("rm " + tempFilename);
			}
		} else {
			log.debug(String.format("SCP: [remote]%s -> [local]%s", args.remoteSourceFilename, targetLocalPath));
			try {
				getSCPClient().get(args.remoteSourceFilename, targetLocalPath);
			} catch (Exception e) {
				result.isSuccess = false;
				result.errorMessage = "SCP failed: " + e.getClass().getSimpleName() + "," + e.getMessage();
				return result;
			}
		}
		if (isTargetLocalPath) {
			result.localTargetFilename = targetLocalPath + sourceRemoteShortFilename;
		} else {
			Files.move(targetLocalPath + sourceRemoteShortFilename, args.localTargetPathOrFilename);
			result.localTargetFilename = args.localTargetPathOrFilename;
		}
		//
		if (args.decompressAfterDownload) {
			Processes.execute("bzip2 -d " + result.localTargetFilename);
			result.localTargetFilename = result.localTargetFilename.substring(0,
					result.localTargetFilename.lastIndexOf('.'));
		}
		if (Files.isExist(result.localTargetFilename)) {
			result.isSuccess = true;
		} else {
			result.isSuccess = false;
			result.errorMessage = "SCP failed: target local file not exist.";
		}
		return result;
	}

	// ///////////////////////////////////////////
	//
	// ///////////////////////////////////////////
	private SFTPv3Client _sftpv3Client;

	private SFTPv3Client getSFTPv3Client() {
		if (null == _sftpv3Client) {
			try {
				_sftpv3Client = new SFTPv3Client(conn);
			} catch (IOException e) {
				throw Exceptions.newInstance(e);
			}
		}
		return _sftpv3Client;
	}

	public void createRemoteDirectory(String remoteDirectory) {
		try {
			log.debug("Create remote path: " + remoteDirectory);
			getSFTPv3Client().mkdir(remoteDirectory, 6);
		} catch (IOException e) {
			throw Exceptions.newInstance(e);
		}
	}

	public void deleteRemoteDirectory(String remoteDirectory) {
		try {
			log.debug("Delete remote path: " + remoteDirectory);
			getSFTPv3Client().rmdir(remoteDirectory);
		} catch (IOException e) {
			throw Exceptions.newInstance(e);
		}
	}

	private final static int SHORT_TEXT_MAX_LENGTH = 1024 * 1024;

	public SFTPv3FileAttributes readRemoteFileAttributes(String remoteFilename) {
		SFTPv3FileHandle h = null;
		try {
			h = getSFTPv3Client().openFileRO(remoteFilename);
			return getSFTPv3Client().fstat(h);
		} catch (IOException e) {
			throw Exceptions.newInstance(e);
		} finally {
			if (null != h) {
				try {
					getSFTPv3Client().closeFile(h);
				} catch (IOException e) {
				}
			}
		}
	}

	public String readRemoteShortText(String remoteFilename) {
		SFTPv3FileHandle h = null;
		try {
			h = getSFTPv3Client().openFileRO(remoteFilename);
			SFTPv3FileAttributes a = getSFTPv3Client().fstat(h);
			if (a.isDirectory()) {
				throw Exceptions.newInstance("Target is a directory, can't read as text: " + remoteFilename);
			}
			if (a.size > SHORT_TEXT_MAX_LENGTH) {
				throw Exceptions.newInstance("Content is too long. Should smaller than " + SHORT_TEXT_MAX_LENGTH);
			}
			StringBuilder sb = new StringBuilder();
			byte[] temp = new byte[1024];
			int total = 0;
			while (true) {
				int count = getSFTPv3Client().read(h, total, temp, 0, 1024);
				if (count <= 0) {
					return sb.toString();
				}
				if (count < 1024) {
					sb.append(Arrays.copyOf(temp, count));
					return sb.toString();
				}
				sb.append(temp);
				total += count;
			}
		} catch (IOException e) {
			throw Exceptions.newInstance(e);
		} finally {
			if (null != h) {
				try {
					getSFTPv3Client().closeFile(h);
				} catch (IOException e) {
				}
			}
		}
	}

	public void writeRemoteShortText(String remoteFilename, String content) {
		if (content.length() > SHORT_TEXT_MAX_LENGTH) {
			throw Exceptions.newInstance("Content is too long. Should smaller than " + SHORT_TEXT_MAX_LENGTH);
		}
		SFTPv3FileHandle h = null;
		try {
			h = getSFTPv3Client().openFileRW(remoteFilename);
			byte[] temp = content.getBytes(Encodings.UTF8.value);
			getSFTPv3Client().write(h, 0, temp, 0, temp.length);
		} catch (IOException e) {
			throw Exceptions.newInstance(e);
		} finally {
			if (null != h) {
				try {
					getSFTPv3Client().closeFile(h);
				} catch (IOException e) {
				}
			}
		}
	}

}
