package com.jspring.patterns.behavioral.commands;

public final class DemoInvoker extends Invoker<DemoReceiver> implements
        IDemoReceiver {
    private static final long serialVersionUID = 1L;

    private DemoInvoker(DemoReceiver receiver) {
        super(receiver);
    }

    @Override
    public void act1(int a) {
        add(new Command<DemoReceiver, Integer>(receiver, a) {
            @Override
            public void execute() {
                receiver.act1(getContext());
            }

            @Override
            public void rollback() {
                receiver.rollbackActions.act1(getContext());
            }

        });
    }

    @Override
    public void act2(String b) {
        add(new Command<DemoReceiver, String>(receiver, b) {
            @Override
            public void execute() {
                receiver.act2(getContext());
            }

            @Override
            public void rollback() {
                receiver.rollbackActions.act2(getContext());
            }

        });
    }

    private static class Act3Args {
        public final int a;
        public final String b;

        public Act3Args(int a, String b) {
            this.a = a;
            this.b = b;
        }
    }

    @Override
    public void act3(int a, String b) {
        add(new Command<DemoReceiver, Act3Args>(receiver, new Act3Args(a, b)) {
            @Override
            public void execute() {
                receiver.act3(getContext().a, getContext().b);
            }

            @Override
            public void rollback() {
                receiver.rollbackActions.act3(getContext().a, getContext().b);
            }

        });
    }

    public static DemoInvoker newInvoker(DemoReceiver receiver) {
        return new DemoInvoker(receiver);
    }
}
