package model.statements;

import exceptions.ADTException;
import exceptions.ExpressionException;
import exceptions.StatementException;
import model.ADT.MyIMap;
import model.expressions.IExpression;
import model.expressions.NotExpression;
import model.state.PrgState;
import model.types.BoolType;
import model.types.IType;

public class RepeatUntilStatement implements IStatement {
    private IStatement stmt;
    private IExpression exp;

    public RepeatUntilStatement(IStatement stmt, IExpression exp) {
        this.stmt = stmt;
        this.exp = exp;
    }

    @Override
    public PrgState execute(PrgState state) throws StatementException, ADTException, ExpressionException {
        IStatement transformed = new CompStatement(
                stmt.deepCopy(),
                new WhileStatement(new NotExpression(exp.deepCopy()), stmt.deepCopy())
        );
        state.getExeStack().push(transformed);
        return null;
    }

    @Override
    public MyIMap<String, IType> typeCheck(MyIMap<String, IType> typeEnv) throws StatementException, ADTException, ExpressionException {
        IType expType = exp.typeCheck(typeEnv);
        if (!expType.equals(new BoolType()))
            throw new StatementException("Repeat condition must be boolean.");
        return stmt.typeCheck(typeEnv.deepCopy());
    }

    @Override
    public IStatement deepCopy() {
        return new RepeatUntilStatement(stmt.deepCopy(), exp.deepCopy());
    }

    @Override
    public String toString() {
        return "repeat " + stmt + " until " + exp;
    }
}