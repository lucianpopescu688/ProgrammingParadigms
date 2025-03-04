package controller;

import exceptions.ADTException;
import exceptions.ControllerException;
import exceptions.ExpressionException;
import exceptions.StatementException;
import model.ADT.MyIHeap;
import model.ADT.MyIMap;
import model.ADT.MyIStack;
import model.state.PrgState;
import model.statements.IStatement;
import model.statements.NopStatement;
import model.values.IValue;
import repo.IRepository;
import repo.Repository;

import java.util.ArrayList;
import java.util.List;

import java.util.Objects;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class Controller {
    private IRepository repo;
    private ExecutorService executor;
    private boolean displayFlag = false;
    private boolean isAlive = true;

    public Controller(IRepository repo, ExecutorService executor) {
        this.repo = repo;
        this.executor = executor;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public void setDisplayFlag(boolean flag) {
        this.displayFlag = flag;
    }

    public boolean getDisplayFlag() {
        return this.displayFlag;
    }

    public void addPRGState(PrgState state) {
        this.repo.add(state);
    }

    public List<PrgState> removeCompletedPrograms(List<PrgState> programs) {
        return programs.stream().filter(PrgState::isNotCompleted).collect(Collectors.toList());
        //NOT .toList BECAUSE IT CREATES AN IMMUTABLE LIST, IT MUST BE .collect(Collectors.toList())
    }

    public void oneStepForAll(List<PrgState> programList) throws ControllerException {
        programList.forEach(state -> this.repo.logProgramState(state));

        List<Callable<PrgState>> callList = programList.stream().
                map((PrgState p) -> (Callable<PrgState>) (() -> {
                    return p.oneStep();
                })).
                collect(Collectors.toList());
        try {
            List<PrgState> newProgramList = executor.invokeAll(callList).stream().
                    map(future -> {
                        try {
                            return future.get();
                        } catch (InterruptedException | ExecutionException e) {
                            throw new RuntimeException(e.getMessage());
                        }
                    }).filter(Objects::nonNull).collect(Collectors.toList());
            newProgramList.stream().forEach(state -> programList.add(state));//exception from here
        } catch (InterruptedException | RuntimeException e) {
            throw new ControllerException(e.getMessage());
        }
        programList.forEach(state -> this.repo.logProgramState(state));
        repo.setProgramList(programList);
    }

    public void allStep() throws ADTException, StatementException, ExpressionException, ControllerException {
        executor = Executors.newFixedThreadPool(2);
        List<PrgState> programList = this.removeCompletedPrograms(this.repo.getProgramList());
        while (!programList.isEmpty()) {
            List<MyIMap<String, IValue>> symTableList = this.repo.getProgramList().stream().map(prg -> prg.getSymTable()).collect(Collectors.toList());
            //List<MyIMap<String, IValue>> symTableList = programList.stream().map(prg->prg.getSymTable()).collect(Collectors.toList());
            GarbageCollector garbageCollector = new GarbageCollector(symTableList, programList.get(0).getHeap());
            MyIMap newHeap = garbageCollector.safeGarbageCollector(garbageCollector.getAddressesFromSymTable());
            programList.forEach(prgState -> prgState.getHeap().setHeap(newHeap));
            oneStepForAll(programList);
            programList = this.removeCompletedPrograms(this.repo.getProgramList());
        }
        executor.shutdownNow();
        repo.setProgramList(programList);
    }

    public void buttonPressed() throws ADTException, StatementException, ExpressionException, ControllerException {
        List<PrgState> programList = this.removeCompletedPrograms(this.repo.getProgramList());
        if (programList.isEmpty()) {
            System.out.println("Program List is empty");
            isAlive = false;
            return;
        }
        executor = Executors.newFixedThreadPool(2);
        List<MyIMap<String, IValue>> symTableList = this.repo.getProgramList().stream().map(prg -> prg.getSymTable()).collect(Collectors.toList());
        //List<MyIMap<String, IValue>> symTableList = programList.stream().map(prg->prg.getSymTable()).collect(Collectors.toList());
        GarbageCollector garbageCollector = new GarbageCollector(symTableList, programList.get(0).getHeap());
        MyIMap newHeap = garbageCollector.safeGarbageCollector(garbageCollector.getAddressesFromSymTable());
        programList.forEach(prgState -> prgState.getHeap().setHeap(newHeap));
        oneStepForAll(programList);
        programList = this.removeCompletedPrograms(this.repo.getProgramList());
        if (programList.isEmpty()) {
            executor.shutdownNow();
            repo.setProgramList(programList);
            isAlive = false;
            return;
        }
        isAlive = true;
    }

    public List<PrgState> getProgramList() {
        return this.repo.getProgramList();
    }
}