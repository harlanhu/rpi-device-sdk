package cn.tpkf.pi.function;

import cn.tpkf.pi.enums.FunctionStateEnums;
import cn.tpkf.pi.exception.FunctionException;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Harlan
 * @email isharlan.hu@gmali.com
 * @date 2023/11/22
 */
public abstract class AbstractFunction implements Function {

    protected final String id;

    protected final String name;

    protected final Integer weight;

    protected final Long executionInterval;

    protected final List<AbstractFunctionCommand> commands;

    protected final ReentrantLock lock;

    protected final Long executeTimeOut;

    protected AtomicReference<FunctionStateEnums> state;

    protected AtomicInteger currentCommandIndex;

    protected AbstractFunction(String id, String name, Integer weight, Long executionInterval, List<AbstractFunctionCommand> commands) {
        this(id, name, weight, executionInterval, commands, 0, 2000L);
    }

    protected AbstractFunction(String id, String name, Integer weight, Long executionInterval, List<AbstractFunctionCommand> commands, Integer commandIndex, Long executeTimeOut) {
        this.id = id;
        this.name = name;
        this.weight = weight;
        this.executionInterval = executionInterval;
        this.commands = commands;
        this.lock = new ReentrantLock();
        this.state = new AtomicReference<>(FunctionStateEnums.INIT);
        this.currentCommandIndex = new AtomicInteger(commandIndex);
        this.executeTimeOut = executeTimeOut;
    }

    @Override
    public FunctionStateEnums getCurrentState() {
        return state.getAcquire();
    }

    @Override
    public AbstractFunctionCommand getCurrentCommand() {
        return commands.get(currentCommandIndex.getAcquire());
    }

    @Override
    public Integer getCurrentCommandIndex() {
        return currentCommandIndex.getAcquire();
    }

    @Override
    public AbstractFunctionCommand getCommand(Integer index) {
        return commands.get(index);
    }

    @Override
    public AbstractFunctionCommand getNextCommand() {
        if (currentCommandIndex.getAcquire() >= commands.size()) {
            return commands.get(0);
        }
        return commands.get(currentCommandIndex.getAcquire());
    }

    @Override
    public Integer getCommandSize() {
        return commands.size();
    }

    @Override
    public void sequenceExecute() {
        if (state.getAcquire().equals(FunctionStateEnums.RUNNING)) {
            throw new FunctionException("The function is currently executing.");
        }
        try {
            if (lock.tryLock(executeTimeOut, TimeUnit.MICROSECONDS)) {
                state.set(FunctionStateEnums.RUNNING);
                int index = currentCommandIndex.getAcquire();
                while (state.getAcquire().equals(FunctionStateEnums.RUNNING)) {
                    currentCommandIndex.set(index);
                    AbstractFunctionCommand command = commands.get(index);
                    command.execute();
                    if (index == commands.size() - 1) {
                        index = 0;
                    } else {
                        index ++;
                    }
                }
            } else {
                throw new FunctionException("The function is currently executing.");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new FunctionException("Fail to acquire lock", e);
        }
    }

    @Override
    public void sequenceExecute(Integer index) {
        currentCommandIndex.set(index);
        sequenceExecute();
    }

    @Override
    public void stopExecute() {
        state.set(FunctionStateEnums.STOPPED);
    }

    @Override
    public void reExecute() {
        if (state.getAcquire().equals(FunctionStateEnums.RUNNING)) {
            stopExecute();
        }
        sequenceExecute(currentCommandIndex.getAcquire());
    }

    @Override
    public void restartExecute() {
        if (state.getAcquire().equals(FunctionStateEnums.RUNNING)) {
            stopExecute();
        }
        currentCommandIndex.set(0);
        sequenceExecute();
    }

    @Override
    public Boolean isRunning() {
        return state.getAcquire().equals(FunctionStateEnums.RUNNING);
    }
}
