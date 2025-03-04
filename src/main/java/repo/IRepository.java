package repo;

import exceptions.RepoException;
import model.state.PrgState;

import java.util.List;

public interface IRepository {
    void add(PrgState state);
    void logProgramState(PrgState state) throws RepoException;
    List<PrgState> getProgramList() throws RepoException;
    void setProgramList(List<PrgState> states) throws RepoException;
}
