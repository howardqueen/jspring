package com.jspring.utils;

import com.jspring.Exceptions;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;

public abstract class Compressors {

	public byte[] compress(byte[] data) {
		ByteArrayInputStream bais = null;
		ByteArrayOutputStream baos = null;
		try {
			bais = new ByteArrayInputStream(data);
			baos = new ByteArrayOutputStream();
			compress(bais, baos);
			byte[] output = baos.toByteArray();
			try {
				baos.flush();
			} catch (Exception e) {
				throw Exceptions.newInstance(e);
			}
			return output;
		} finally {
			if (null != baos) {
				try {
					baos.close();
				} catch (Exception e) {
				}
			}
			if (null != bais) {
				try {
					bais.close();
				} catch (Exception e) {
				}
			}
		}
	}

	public abstract void compress(InputStream is, OutputStream os);

	public void compress(String source, String target) {
		FileInputStream fis = null;
		FileOutputStream fos = null;
		try {
			fis = new FileInputStream(source);
			fos = new FileOutputStream(target);
			compress(fis, fos);
			fos.flush();
		} catch (Exception e) {
			throw Exceptions.newInstance(e);
		} finally {
			if (null != fis) {
				try {
					fis.close();
				} catch (Exception e) {
				}
			}
			if (null != fos) {
				try {
					fos.close();
				} catch (Exception e) {
				}
			}
		}
	}

	public byte[] decompress(byte[] data) {
		ByteArrayInputStream bais = null;
		ByteArrayOutputStream baos = null;
		try {
			bais = new ByteArrayInputStream(data);
			baos = new ByteArrayOutputStream();
			decompress(bais, baos);
			data = baos.toByteArray();
			try {
				baos.flush();
			} catch (Exception e) {
				throw Exceptions.newInstance(e);
			}
			return data;
		} finally {
			if (null != baos) {
				try {
					baos.close();
				} catch (Exception e) {
				}
			}
			if (null != bais) {
				try {
					bais.close();
				} catch (Exception e) {
				}
			}
		}
	}

	public abstract void decompress(InputStream is, OutputStream os);

	public void decompress(String source, String target) {
		FileInputStream fis = null;
		FileOutputStream fos = null;
		try {
			fis = new FileInputStream(source);
			fos = new FileOutputStream(target);
			decompress(fis, fos);
			fos.flush();
		} catch (Exception e) {
			throw Exceptions.newInstance(e);
		} finally {
			if (null != fis) {
				try {
					fis.close();
				} catch (Exception e) {
				}
			}
			if (null != fos) {
				try {
					fos.close();
				} catch (Exception e) {
				}
			}
		}
	}

	private static class BZip2 extends Compressors {

		@Override
		public void compress(InputStream is, OutputStream os) {
			BZip2CompressorOutputStream gos = null;
			try {
				gos = new BZip2CompressorOutputStream(os);
				int count;
				byte data[] = new byte[1024];// bufferSize:1024
				while ((count = is.read(data, 0, data.length)) != -1) {
					gos.write(data, 0, count);
				}
				gos.finish();
				gos.flush();
			} catch (Exception e) {
				throw Exceptions.newInstance(e);
			} finally {
				if (null != gos) {
					try {
						gos.close();
					} catch (Exception e) {
					}
				}
			}
		}

		@Override
		public void decompress(InputStream is, OutputStream os) {
			BZip2CompressorInputStream gis = null;
			try {
				gis = new BZip2CompressorInputStream(is);
				int count;
				byte data[] = new byte[1024];// bufferSize:1024
				while ((count = gis.read(data, 0, data.length)) != -1) {
					os.write(data, 0, count);
				}
			} catch (Exception e) {
				throw Exceptions.newInstance(e);
			} finally {
				if (null != gis) {
					try {
						gis.close();
					} catch (Exception e) {
					}
				}
			}
		}

	}

	public static Compressors newBZip2() {
		return new BZip2();
	}

	public static class GZip extends Compressors {

		@Override
		public void compress(String source, String target) {
			FileInputStream fis = null;
			FileOutputStream fos = null;
			ZipOutputStream zos = null;
			try {
				fis = new FileInputStream(source);
				fos = new FileOutputStream(target);
				//
				zos = new ZipOutputStream(fos);
				zos.putNextEntry(new ZipEntry(source));
				int size = 0;
				byte[] buffer = new byte[1024];

				// r读去数据并写入zip文件
				while ((size = fis.read(buffer, 0, buffer.length)) > 0) {
					zos.write(buffer, 0, size);
				}

				fos.flush();
			} catch (Exception e) {
				throw Exceptions.newInstance(e);
			} finally {
				if (null != zos) {
					try {
						zos.close();
					} catch (Exception e) {
					}
				}
				if (null != fis) {
					try {
						fis.close();
					} catch (Exception e) {
					}
				}
				if (null != fos) {
					try {
						fos.close();
					} catch (Exception e) {
					}
				}
			}
		}

		@Override
		public void compress(InputStream is, OutputStream os) {
			throw Exceptions.newInstance("not supported for GZip.compress(InputStream, OutputStream)");
		}

		@Override
		public void decompress(InputStream is, OutputStream os) {
			try {
				ZipInputStream zin = new ZipInputStream(is);
				ZipEntry entry = zin.getNextEntry();
				while (entry != null) {
					byte[] buf = new byte[2048];
					int len = 0;
					while ((len = zin.read(buf)) > 0) {
						os.write(buf, 0, len);
					}
					os.flush();
					os.close();
					entry = zin.getNextEntry();
				}
				zin.closeEntry();
			} catch (Exception e) {
				throw Exceptions.newInstance(e);
			}
		}

		public void unzipFiles(String savePath, String... zipFilenames) {
			for (String filename : zipFilenames) {
				try {
					File file = new File(savePath);
					ZipInputStream zin = new ZipInputStream(new FileInputStream(filename));
					ZipEntry entry = zin.getNextEntry();
					while (entry != null) {
						File child = new File(file, entry.getName());
						FileOutputStream fos = new FileOutputStream(child);
						byte[] buf = new byte[2048];
						int len = 0;
						while ((len = zin.read(buf)) > 0) {
							fos.write(buf, 0, len);
						}
						fos.flush();
						fos.close();
						entry = zin.getNextEntry();
					}
					zin.closeEntry();
					zin.close();
				} catch (Exception e) {
					throw Exceptions.newInstance(e);
				}
			}
		}

	}

	public static GZip newGZip() {
		return new GZip();
	}

}
