package model.statements;

import exceptions.ADTException;
import exceptions.ExpressionException;
import exceptions.StatementException;
import model.ADT.MyIMap;
import model.expressions.IExpression;
import model.state.PrgState;
import model.types.BoolType;
import model.types.IType;
import model.values.BoolValue;
import model.values.IValue;

public class IfStatement implements IStatement {
   private IExpression condition;
   private IStatement thenStatement;
   private IStatement elseStatement;
   public IfStatement(IExpression condition, IStatement thenStatement, IStatement elseStatement) {
       this.condition = condition;
       this.thenStatement = thenStatement;
       this.elseStatement = elseStatement;
   }
   public PrgState execute(PrgState state) throws StatementException, ADTException, ExpressionException {
       IValue v = condition.eval(state.getSymTable(), state.getHeap());
       if (v.getType().equals(new BoolType()))
       {
           if (((BoolValue) v).getValue())
           {
               state.getExeStack().push(thenStatement);
           }
           else
           {
               state.getExeStack().push(elseStatement);
           }
           return null;
       }
       else
       {
           throw new StatementException("Condition expression does not evaluate bool");
       }
   }
   public String toString() {
       return "if(" + condition.toString() + ") {" + thenStatement.toString() + "} else {" + elseStatement.toString() + "}";
   }

   public IStatement deepCopy() {
       return new IfStatement(condition.deepCopy(), thenStatement.deepCopy(), elseStatement.deepCopy());
   }

   public MyIMap<String, IType> typeCheck(MyIMap<String, IType> typeEnvironment) throws StatementException, ADTException, ExpressionException
   {
       if (condition.typeCheck(typeEnvironment).equals(new BoolType()))
       {
           thenStatement.typeCheck(typeEnvironment.deepCopy());
           elseStatement.typeCheck(typeEnvironment.deepCopy());
           return typeEnvironment;
       }
       throw new StatementException("The condition of the if is not a boolean");
   }
}
