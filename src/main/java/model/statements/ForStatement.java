package model.statements;

import exceptions.ADTException;
import exceptions.ExpressionException;
import exceptions.StatementException;
import model.ADT.MyIMap;
import model.expressions.*;
import model.state.PrgState;
import model.types.IntType;
import model.types.IType;
import model.values.IValue;

public class ForStatement implements IStatement {
    private String variable;
    private IExpression exp1;
    private IExpression exp2;
    private IExpression exp3;
    private IStatement statement;

    public ForStatement(String variable, IExpression exp1, IExpression exp2, IExpression exp3, IStatement statement) {
        this.variable = variable;
        this.exp1 = exp1;
        this.exp2 = exp2;
        this.exp3 = exp3;
        this.statement = statement;
    }

    @Override
    public PrgState execute(PrgState state) throws StatementException, ADTException, ExpressionException {
        IStatement transformed = new CompStatement(
                new VarDecStatement(variable, new IntType()),
                new CompStatement(
                        new AssignmentStatement(variable, exp1),
                        new WhileStatement(
                                new RelationalExpression(
                                        new VariableEvalExpression(variable),
                                        RelationalOperator.LESS_THAN,
                                        exp2
                                ),
                                new CompStatement(
                                        statement,
                                        new AssignmentStatement(variable, exp3)
                                )
                        )
                )
        );
        state.getExeStack().push(transformed);
        return null;
    }

    @Override
    public MyIMap<String, IType> typeCheck(MyIMap<String, IType> typeEnv) throws StatementException, ADTException, ExpressionException {
        MyIMap<String, IType> newEnv = typeEnv.deepCopy();
        newEnv.put(variable, new IntType());

        IType typeExp1 = exp1.typeCheck(newEnv);
        IType typeExp2 = exp2.typeCheck(newEnv);
        IType typeExp3 = exp3.typeCheck(newEnv);

        if (!typeExp1.equals(new IntType()) || !typeExp2.equals(new IntType()) || !typeExp3.equals(new IntType())) {
            throw new StatementException("For statement expressions must be integers.");
        }

        statement.typeCheck(newEnv.deepCopy());
        return typeEnv;
    }

    @Override
    public IStatement deepCopy() {
        return new ForStatement(variable, exp1.deepCopy(), exp2.deepCopy(), exp3.deepCopy(), statement.deepCopy());
    }

    @Override
    public String toString() {
        return String.format("for(%s=%s; %s<%s; %s=%s) %s",
                variable, exp1, variable, exp2, variable, exp3, statement);
    }
}