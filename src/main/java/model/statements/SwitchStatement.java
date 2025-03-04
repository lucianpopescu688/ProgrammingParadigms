package model.statements;

import exceptions.ADTException;
import exceptions.ExpressionException;
import exceptions.StatementException;
import model.ADT.MyIMap;
import model.expressions.IExpression;
import model.expressions.RelationalExpression;
import model.expressions.RelationalOperator;
import model.state.PrgState;
import model.types.IType;

public class SwitchStatement implements IStatement {
    private IExpression exp;
    private IExpression exp1;
    private IStatement stmt1;
    private IExpression exp2;
    private IStatement stmt2;
    private IStatement stmt3;

    public SwitchStatement(IExpression exp, IExpression exp1, IStatement stmt1, IExpression exp2, IStatement stmt2, IStatement stmt3) {
        this.exp = exp;
        this.exp1 = exp1;
        this.stmt1 = stmt1;
        this.exp2 = exp2;
        this.stmt2 = stmt2;
        this.stmt3 = stmt3;
    }

    @Override
    public PrgState execute(PrgState state) throws StatementException, ADTException, ExpressionException {
        IStatement newStmt = new IfStatement(
                new RelationalExpression(exp.deepCopy(), RelationalOperator.EQUAL, exp1.deepCopy()),
                stmt1.deepCopy(),
                new IfStatement(
                        new RelationalExpression(exp.deepCopy(), RelationalOperator.EQUAL, exp2.deepCopy()),
                        stmt2.deepCopy(),
                        stmt3.deepCopy()
                )
        );
        state.getExeStack().push(newStmt);
        return null;
    }

    @Override
    public MyIMap<String, IType> typeCheck(MyIMap<String, IType> typeEnv) throws StatementException, ADTException, ExpressionException {
        IType typeExp = exp.typeCheck(typeEnv);
        IType typeExp1 = exp1.typeCheck(typeEnv);
        IType typeExp2 = exp2.typeCheck(typeEnv);

        if (!typeExp.equals(typeExp1) || !typeExp.equals(typeExp2)) {
            throw new StatementException("Switch expressions must have the same type.");
        }

        stmt1.typeCheck(typeEnv.deepCopy());
        stmt2.typeCheck(typeEnv.deepCopy());
        stmt3.typeCheck(typeEnv.deepCopy());

        return typeEnv;
    }

    @Override
    public IStatement deepCopy() {
        return new SwitchStatement(
                exp.deepCopy(),
                exp1.deepCopy(),
                stmt1.deepCopy(),
                exp2.deepCopy(),
                stmt2.deepCopy(),
                stmt3.deepCopy()
        );
    }

    @Override
    public String toString() {
        return String.format("switch(%s) (case %s: %s) (case %s: %s) (default: %s)",
                exp, exp1, stmt1, exp2, stmt2, stmt3);
    }
}