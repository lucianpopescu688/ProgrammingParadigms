package com.example.toyInterpreter;

import controller.Controller;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.ADT.*;
import model.expressions.*;
import model.state.PrgState;
import model.statements.*;
import model.types.*;
import model.values.BoolValue;
import model.values.IValue;
import model.values.IntValue;
import model.values.StringValue;
import repo.IRepository;
import repo.Repository;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HelloApplication extends Application {
    @Override
    public void start(Stage s) throws IOException {
        s.setTitle("Toy Language Interpreter");

        System.out.println("Program started");

        // Create statements
        List<IStatement> statements = createStatements();

        // Type-check all statements
        try {
            for (IStatement stmt : statements) {
                stmt.typeCheck(new MyMap<>());
            }
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("TypeChecker Failed");
            alert.setHeaderText("TypeChecker Failed");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            System.exit(1);
        }

        // Create program states
        List<PrgState> programStates = new ArrayList<>();
        for (IStatement stmt : statements) {
            programStates.add(new PrgState(stmt));
        }

        // Create repositories, executors, and controllers
        List<IRepository> repositories = createRepositories(programStates);
        List<ExecutorService> executors = createExecutors(statements.size());
        List<Controller> controllers = createControllers(repositories, executors);

        ObservableList<String> programs = createProgramList(statements);
        // create a label
        Label selectProgramStateLabel = new Label("Select Program State");
        selectProgramStateLabel.setStyle("-fx-text-fill: black; -fx-font-size: 16px; -fx-font-weight: bold;");
        selectProgramStateLabel.setAlignment(Pos.CENTER);

// create a table view for program states
        TableView<String> programStatesTable = new TableView<>();
        TableColumn<String, String> stateColumn = new TableColumn<>("Program State");
        stateColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()));
        programStatesTable.getColumns().add(stateColumn);
        programStatesTable.setItems(programs);
        programStatesTable.setStyle("-fx-background-color: #ffffff;");
        programStatesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        String componentStyle = "-fx-border-color: black; " +
                "-fx-border-width: 1px; " +
                "-fx-border-radius: 3px; " +
                "-fx-padding: 5px;";

// create a button
        Button b = new Button("Run selected program");
        b.setAlignment(Pos.CENTER);
        b.setStyle(
                "-fx-border-color: black;" +
                        "-fx-border-width: 2px;" +
                        "-fx-background-color: #f0f0f0;" +
                        "-fx-text-fill: black;" +
                        "-fx-font-size: 16px;"
        );

        // create a stack pane
        VBox r = new VBox(10); // Add spacing between elements
        r.setAlignment(Pos.CENTER);
        r.setStyle("-fx-background-color: #ffffff; -fx-padding: 20px;");

        ListView<String> lv = new ListView<>(programs);
        lv.setStyle("-fx-background-color: #ffffff;");
        lv.setCellFactory(lvw -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                    setStyle("-fx-background-color: transparent;");
                } else {
                    setText(item);
                    setPadding(new Insets(10, 5, 10, 5));
                    if (isSelected()) {
                        setStyle("-fx-background-color: #d3d3d3;" +
                                "-fx-text-fill: #000000;" +
                                "-fx-alignment: CENTER;" +
                                "-fx-border-color: black;" +
                                "-fx-border-width: 2px;" +
                                "-fx-font-size: 16px;" +
                                "-fx-font-weight: bold;"
                        );
                    } else {
                        setStyle("-fx-background-color: #ffffff;" +
                                "-fx-text-fill: #000000;" +
                                "-fx-alignment: CENTER;" +
                                "-fx-font-size: 16px;"
                        );
                    }
                }
            }
        });

        // action event
        EventHandler<ActionEvent> event = new EventHandler<ActionEvent>() {
            int id = 0;

            public static class TableEntry {
                private final SimpleStringProperty field1;
                private final SimpleStringProperty field2;

                public TableEntry(String field1, String field2) {
                    this.field1 = new SimpleStringProperty(field1);
                    this.field2 = new SimpleStringProperty(field2);
                }

                public SimpleStringProperty getField1() {
                    return field1;
                }

                public SimpleStringProperty getField2() {
                    return field2;
                }
            }

            public void handle(ActionEvent e) {
                int i = programStatesTable.getSelectionModel().getSelectedIndex();
                //TableEntry te = new TableEntry(String.valueOf(i), "meow");
                if (i >= 0) {
                    Stage newWindow = new Stage();

                    Controller ctrl = controllers.get(i);
                    PrgState s = ctrl.getProgramList().get(0);
                    id = s.getId();

                    TextField nrOfPrograms = new TextField("Number of program states: " + ctrl.getProgramList().size());
                    nrOfPrograms.setEditable(false);
                    TableView<TableEntry> heapTableWidget = new TableView<>();
                    ListView<String> outListWidget = new ListView<>();
                    ListView<String> fileTableWidget = new ListView<>();
                    ObservableList<String> states = FXCollections.observableArrayList();
                    states.add(ctrl.getProgramList().get(0).getId() + "");
                    ListView<String> programStatesWidget = new ListView<>(states);
                    TableView<TableEntry> symTableWidget = new TableView<>();
                    ListView<String> exeStackWidget = new ListView<>();
                    TableView<TableEntry> lockTableWidget = new TableView<>();

                    ObservableList<TableEntry> heapTableEntries = FXCollections.observableArrayList();
                    ObservableList<TableEntry> symTableEntries = FXCollections.observableArrayList();

                    TableColumn<TableEntry, String> addressColumn = new TableColumn<>("Address");
                    TableColumn<TableEntry, String> hValueColumn = new TableColumn<>("Value");
                    TableColumn<TableEntry, String> variableColumn = new TableColumn<>("Variable");
                    TableColumn<TableEntry, String> sValueColumn = new TableColumn<>("Value");
                    TableColumn<TableEntry, String> lockColumn = new TableColumn<>("Lock");
                    TableColumn<TableEntry, String> lockValueColumn = new TableColumn<>("Value");

                    addressColumn.setCellValueFactory(entry -> entry.getValue().getField1());
                    hValueColumn.setCellValueFactory(entry -> entry.getValue().getField2());

                    variableColumn.setCellValueFactory(entry -> entry.getValue().getField1());
                    sValueColumn.setCellValueFactory(entry -> entry.getValue().getField2());


                    lockColumn.setCellValueFactory(entry -> entry.getValue().getField1());
                    lockValueColumn.setCellValueFactory(entry -> entry.getValue().getField2());

                    heapTableWidget.getColumns().addAll(addressColumn, hValueColumn);
                    symTableWidget.getColumns().addAll(variableColumn, sValueColumn);
                    lockTableWidget.getColumns().addAll(lockColumn, lockValueColumn);

                    MyIStack<IStatement> st = s.getExeStack();
                    st.toList().stream().forEach(statement ->
                    {
                        exeStackWidget.getItems().add(statement.toString());
                    });

                    programStatesWidget.setOnMouseClicked(event -> {
                        //index = programStatesWidget.getSelectionModel().getSelectedIndex();
                        String aux = programStatesWidget.getSelectionModel().getSelectedItem();
                        id = Integer.parseInt(aux);
                        //System.out.println(id);
                        if (id < 0) id = 0;
                        exeStackWidget.getItems().clear();
                        //PrgState state = ctrl.getProgramList().get(id);

                        PrgState state = ctrl.getProgramList().get(0);
                        for (int iter = 0; iter < ctrl.getProgramList().size(); iter++) {
                            state = ctrl.getProgramList().get(iter);
                            if (state.getId() == id) break;
                        }

                        lockTableWidget.getItems().clear();
                        ObservableList<TableEntry> lockTableEntries = FXCollections.observableArrayList();
                        MyILockTable lockTable = state.getLockTable();

                        lockTable.getLockTable().forEach((key, value) -> {
                            lockTableEntries.add(new TableEntry(String.valueOf(key), String.valueOf(value)));
                        });

                        lockTableWidget.setItems(lockTableEntries);
                        lockTableWidget.refresh();


                        MyIStack<IStatement> stack = state.getExeStack();
                        stack.toList().stream().forEach(statement ->
                        {
                            exeStackWidget.getItems().add(statement.toString());
                        });

                        symTableWidget.getItems().clear();
                        MyIMap<String, IValue> symTable = state.getSymTable();
                        symTable.getKeys().stream().forEach(key -> {
                            symTableEntries.add(new TableEntry(key, symTable.getValue(key).toString()));
                        });
                        symTableWidget.setItems(symTableEntries);

                    });

                    Button oneStepButton = new Button("One Step");
                    EventHandler<ActionEvent> event = new EventHandler<ActionEvent>() {
                        boolean idk = true;

                        public void handle(ActionEvent e) {
                            exeStackWidget.getItems().clear();
                            programStatesWidget.getItems().clear();
                            MyIList<IValue> outList = new MyList<>();
                            MyIMap<StringValue, BufferedReader> fileTable = new MyMap<>();

                            lockTableWidget.getItems().clear();
                            ObservableList<TableEntry> lockTableEntries = FXCollections.observableArrayList();
                            MyILockTable lockTable = ctrl.getProgramList().get(0).getLockTable();

                            lockTable.getLockTable().forEach((key, value) -> {
                                lockTableEntries.add(new TableEntry(String.valueOf(key), String.valueOf(value)));
                            });

                            lockTableWidget.setItems(lockTableEntries);
                            lockTableWidget.refresh();


                            try {
                                if (ctrl.isAlive()) {
                                    outList = ctrl.getProgramList().get(0).getOutList();
                                    fileTable = ctrl.getProgramList().get(0).getFileTable();
                                    ctrl.buttonPressed();
                                    nrOfPrograms.setText("Number of program states: " + ctrl.getProgramList().size());
                                }
                                if (!ctrl.isAlive() && idk) {
                                    idk = false;
                                    outListWidget.getItems().clear();
                                    outList.getAll().stream().forEach(v -> outListWidget.getItems().add(v.toString()));
                                    outListWidget.refresh();
                                    fileTableWidget.getItems().clear();
                                    fileTable.getKeys().stream().forEach(key -> {
                                        fileTableWidget.getItems().add(key.toString());
                                    });
                                    fileTableWidget.refresh();
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                            if (ctrl.isAlive()) {
                                ctrl.getProgramList().forEach(elem -> programStatesWidget.getItems().add("" + elem.getId()));

                                PrgState state = ctrl.getProgramList().get(0);
                                for (int iter = 0; iter < ctrl.getProgramList().size(); iter++) {
                                    state = ctrl.getProgramList().get(iter);
                                    if (state.getId() == id) break;
                                }

                                MyIStack<IStatement> stack = state.getExeStack();
                                stack.toList().stream().forEach(statement ->
                                {
                                    exeStackWidget.getItems().add(statement.toString());
                                });

                                MyIMap<String, IValue> symTable = state.getSymTable();
                                MyIHeap heapTable = state.getHeap();

                                heapTableWidget.getItems().clear();
                                symTableWidget.getItems().clear();

                                heapTable.getHeap().getKeys().stream().forEach(key -> {
                                    heapTableEntries.add(new TableEntry(String.valueOf(key), heapTable.getValue(key).toString()));
                                });
                                symTable.getKeys().stream().forEach(key -> {
                                    symTableEntries.add(new TableEntry(key, symTable.getValue(key).toString()));
                                });

                                heapTableWidget.setItems(heapTableEntries);
                                symTableWidget.setItems(symTableEntries);

                                outListWidget.getItems().clear();
                                outList.getAll().stream().forEach(v -> outListWidget.getItems().add(v.toString()));
                                outListWidget.refresh();

                                fileTableWidget.getItems().clear();
                                fileTable.getKeys().stream().forEach(key -> {
                                    fileTableWidget.getItems().add(key.toString());
                                });
                                fileTableWidget.refresh();
                            }
                        }
                    };
                    oneStepButton.setOnAction(event);

                    // Replace the existing layout setup with:
                    GridPane layout = new GridPane();
                    layout.setAlignment(Pos.CENTER);
                    layout.setHgap(15);
                    layout.setVgap(15);
                    layout.setPadding(new Insets(15));
                    layout.setStyle("-fx-background-color: #ffffff; " +
                            "-fx-border-color: #cccccc; " +
                            "-fx-border-width: 1px; " +
                            "-fx-padding: 15px;");


                    layout.add(nrOfPrograms, 0, 0);
                    layout.add(programStatesWidget, 1, 0);
                    layout.add(oneStepButton, 2, 0);
                    GridPane.setHalignment(oneStepButton, HPos.CENTER);
                    GridPane.setValignment(oneStepButton, VPos.CENTER);

                    oneStepButton.setStyle(
                            "-fx-border-color: black;" + // MODIFIED
                                    "-fx-border-width: 2px;" +
                                    "-fx-background-color: #f0f0f0;" +
                                    "-fx-text-fill: black;" + // MODIFIED
                                    "-fx-font-size: 16px;"
                    );

                    programStatesWidget.setStyle("-fx-background-color: #ffffff;" + componentStyle);
                    programStatesWidget.setCellFactory(lvw -> new ListCell<String>() {
                        @Override
                        protected void updateItem(String item, boolean empty) {
                            super.updateItem(item, empty);
                            if (item == null || empty) {
                                setText(null);
                                setStyle("-fx-background-color: transparent;");
                            } else {
                                setText(item);
                                //setPadding(new Insets(10, 5, 10, 5));
                                if (isSelected()) {
                                    setStyle("-fx-background-color: #d3d3d3;" +
                                            "-fx-text-fill: #000000;" +
                                            "-fx-alignment: CENTER;" +
                                            "-fx-border-color: white;" +
                                            "-fx-border-width: 2px;" +
                                            "-fx-font-size: 12px;" +
                                            "-fx-font-weight: bold;"
                                    );
                                } else {
                                    setStyle("-fx-background-color: #ffffff;" +
                                            "-fx-text-fill: #000000;" +
                                            "-fx-alignment: CENTER;" +
                                            "-fx-font-size: 12px;"
                                    );
                                }
                            }
                        }

                    });

                    outListWidget.setStyle("-fx-background-color: #ffffff;" + componentStyle);
                    outListWidget.setCellFactory(lv -> new ListCell<String>() {
                        @Override
                        protected void updateItem(String item, boolean empty) {
                            super.updateItem(item, empty);
                            if (empty || item == null) {
                                setText(null);
                                setStyle("-fx-background-color: #ffffff;");
                            } else {
                                setText(item);
                                setStyle("-fx-background-color: #ffffff; " +
                                        "-fx-text-fill: black; " +
                                        "-fx-alignment: CENTER-LEFT;");  // Changed to CENTER-LEFT
                            }
                        }
                    });


                    // For Heap Table
                    // For Heap Table headers
                    heapTableWidget.lookupAll(".column-header").forEach(node ->
                            node.setStyle("-fx-background-color: #ffffff; " +
                                    "-fx-border-color: black; " +
                                    "-fx-border-width: 0 1 1 0; " +
                                    "-fx-text-fill: black;")
                    );

// For Symbol Table headers
                    symTableWidget.lookupAll(".column-header").forEach(node ->
                            node.setStyle("-fx-background-color: #ffffff; " +
                                    "-fx-border-color: black; " +
                                    "-fx-border-width: 0 1 1 0; " +
                                    "-fx-text-fill: black;")
                    );

// For Symbol Table
                    symTableWidget.lookupAll(".column-header").forEach(node ->
                            node.setStyle("-fx-background-color: #ffffff; -fx-text-fill: black;")
                    );
                    symTableWidget.lookupAll(".column-header-background").forEach(node ->
                            node.setStyle("-fx-background-color: #ffffff;")
                    );

                    fileTableWidget.setStyle("-fx-background-color: #ffffff;" + componentStyle);
                    fileTableWidget.setCellFactory(lv -> new ListCell<String>() {
                        @Override
                        protected void updateItem(String item, boolean empty) {
                            super.updateItem(item, empty);
                            if (empty || item == null) {
                                setText(null);
                                setStyle("-fx-background-color: #ffffff;"); // MODIFIED
                            } else {
                                setText(item);
                                setStyle("-fx-background-color: #ffffff; " + // MODIFIED
                                        "-fx-text-fill: black;"); // MODIFIED
                            }
                        }
                    });

                    exeStackWidget.setStyle("-fx-background-color: #ffffff;" + componentStyle);
                    exeStackWidget.setCellFactory(lv -> new ListCell<String>() {
                        @Override
                        protected void updateItem(String item, boolean empty) {
                            super.updateItem(item, empty);
                            if (empty || item == null) {
                                setText(null);
                                setStyle("-fx-background-color: #ffffff;"); // MODIFIED
                            } else {
                                setText(item);
                                setStyle("-fx-background-color: #ffffff; " + // MODIFIED
                                        "-fx-text-fill: black; " + // MODIFIED
                                        "-fx-alignment: CENTER-LEFT;");
                                if (isSelected()) {
                                    setStyle("-fx-background-color: #d3d3d3; " + // MODIFIED
                                            "-fx-text-fill: black;");
                                }
                            }
                        }
                    });

                    heapTableWidget.setStyle("-fx-background-color: #ffffff;" + componentStyle);
                    // Replace the existing heapTableWidget style setup with:
                    heapTableWidget.setStyle("-fx-background-color: #ffffff; " +
                            "-fx-table-header-border-color: black; " +
                            "-fx-border-color: black; " +  // Add this
                            "-fx-border-width: 1px; " +    // Add this
                            componentStyle);

                    heapTableWidget.setRowFactory(tv -> new TableRow<TableEntry>() {
                        @Override
                        protected void updateItem(TableEntry item, boolean empty) {
                            super.updateItem(item, empty);
                            setStyle("-fx-background-color: #ffffff;"); // MODIFIED
                        }
                    });

                    for (TableColumn<TableEntry, ?> column : heapTableWidget.getColumns()) {
                        column.setCellFactory(tc -> new TableCell() {
                            @Override
                            protected void updateItem(Object item, boolean empty) {
                                super.updateItem(item, empty);
                                if (empty || item == null) {
                                    setText(null);
                                } else {
                                    setText(item.toString());
                                    setStyle("-fx-text-fill: black; -fx-alignment: CENTER;");
                                }
                            }
                        });
                    }

                    symTableWidget.setStyle("-fx-background-color: #ffffff;" + componentStyle);
                    symTableWidget.setRowFactory(tv -> new TableRow<TableEntry>() {
                        @Override
                        protected void updateItem(TableEntry item, boolean empty) {
                            super.updateItem(item, empty);
                            setStyle("-fx-background-color: #ffffff;"); // MODIFIED
                        }
                    });

                    for (TableColumn<TableEntry, ?> column : symTableWidget.getColumns()) {
                        column.setCellFactory(tc -> new TableCell() {
                            @Override
                            protected void updateItem(Object item, boolean empty) {
                                super.updateItem(item, empty);
                                if (empty || item == null) {
                                    setText(null);
                                } else {
                                    setText(item.toString());
                                    setStyle("-fx-text-fill: black; -fx-alignment: CENTER;");
                                }
                            }
                        });
                    }

                    layout.setStyle("-fx-background-color: #ffffff;");

                    // Add labels for each table
                    Label heapTableLabel = new Label("Heap Table");
                    heapTableLabel.setStyle("-fx-text-fill: black; -fx-font-size: 16px; -fx-font-weight: bold;");
                    Label outListLabel = new Label("Output List");
                    outListLabel.setStyle("-fx-text-fill: black; -fx-font-size: 16px; -fx-font-weight: bold;");
                    Label fileTableLabel = new Label("File Table");
                    fileTableLabel.setStyle("-fx-text-fill: black; -fx-font-size: 16px; -fx-font-weight: bold;");
                    Label symTableLabel = new Label("Symbol Table");
                    symTableLabel.setStyle("-fx-text-fill: black; -fx-font-size: 16px; -fx-font-weight: bold;");
                    Label exeStackLabel = new Label("Execution Stack");
                    exeStackLabel.setStyle("-fx-text-fill: black; -fx-font-size: 16px; -fx-font-weight: bold;");
                    Label lockTableLabel = new Label("Lock Table");
                    lockTableLabel.setStyle("-fx-text-fill: black; -fx-font-size: 16px; -fx-font-weight: bold;");

                    // Add labels and tables to the layout
                    layout.add(heapTableLabel, 0, 1);
                    layout.add(heapTableWidget, 0, 2);
                    layout.add(outListLabel, 1, 1);
                    layout.add(outListWidget, 1, 2);
                    layout.add(fileTableLabel, 2, 1);
                    layout.add(fileTableWidget, 2, 2);
                    layout.add(symTableLabel, 0, 3);
                    layout.add(symTableWidget, 0, 4);
                    layout.add(exeStackLabel, 1, 3);
                    layout.add(exeStackWidget, 1, 4);
                    layout.add(lockTableLabel, 2, 3);
                    layout.add(lockTableWidget, 2, 4);

                    Scene scene = new Scene(layout, 600, 600);

                    Platform.runLater(() -> layout.requestFocus());//so that the text in the textField is not selected; removes focus from the textField

                    newWindow.setTitle("Program " + (i + 1) + " running");
                    newWindow.setScene(scene);
                    newWindow.show();
                }
            }
        };

        // when button is pressed
        b.setOnAction(event);

        // add button
        r.getChildren().addAll(selectProgramStateLabel, programStatesTable, b);
        // create a scene
        Scene sc = new Scene(r, 800, 600);

        // set the scene
        s.setScene(sc);

        s.show();
    }
    private static List<IStatement> createStatements() {
        List<IStatement> statements = new ArrayList<>();

        statements.add(new CompStatement(new VarDecStatement("v", new IntType()), new CompStatement(new AssignmentStatement("v", new ValueEvalExpression(new IntValue(2))), new PrintStatement(new VariableEvalExpression("v")))));
        statements.add(new CompStatement(new VarDecStatement("a", new IntType()), new CompStatement(new VarDecStatement("b", new IntType()), new CompStatement(new AssignmentStatement("a", new ArithmeticalExpression(new ValueEvalExpression(new IntValue(2)), ArithmeticalOperator.PLUS, new ArithmeticalExpression(new ValueEvalExpression(new IntValue(3)), ArithmeticalOperator.MULTIPLY, new ValueEvalExpression(new IntValue(5))))), new CompStatement(new AssignmentStatement("b", new ArithmeticalExpression(new VariableEvalExpression("a"), ArithmeticalOperator.PLUS, new ValueEvalExpression(new IntValue(1)))), new PrintStatement(new VariableEvalExpression("b")))))));
        statements.add(new CompStatement(new VarDecStatement("a", new BoolType()), new CompStatement(new VarDecStatement("v", new IntType()), new CompStatement(new AssignmentStatement("a", new ValueEvalExpression(new BoolValue(true))), new CompStatement(new IfStatement(new LogicalExpression(new VariableEvalExpression("a"), LogicalOperator.AND, new RelationalExpression(new ValueEvalExpression(new IntValue(1)), RelationalOperator.LESS_EQUAL, new ValueEvalExpression(new IntValue(2)))), new AssignmentStatement("v", new ValueEvalExpression(new IntValue(2))), new AssignmentStatement("v", new ValueEvalExpression(new IntValue(3)))), new PrintStatement(new VariableEvalExpression("v")))))));
        statements.add(new CompStatement(new VarDecStatement("varf", new StringType()), new CompStatement(new AssignmentStatement("varf", new ValueEvalExpression(new StringValue("test.in"))), new CompStatement(new OpenFileStatement(new VariableEvalExpression("varf")), new CompStatement(new VarDecStatement("varc", new IntType()), new CompStatement(new ReadFileStatement(new VariableEvalExpression("varf"), "varc"), new CompStatement(new PrintStatement(new VariableEvalExpression("varc")), new CompStatement(new ReadFileStatement(new VariableEvalExpression("varf"), "varc"), new CompStatement(new PrintStatement(new VariableEvalExpression("varc")), new CloseFileStatement(new VariableEvalExpression("varf")))))))))));
        statements.add(new CompStatement(
                new VarDecStatement("v", new IntType()), new CompStatement(
                new AssignmentStatement("v", new ValueEvalExpression(new IntValue(4))), new CompStatement(
                new WhileStatement(new RelationalExpression(new VariableEvalExpression("v"), RelationalOperator.GREATER_THAN, new ValueEvalExpression(new IntValue(0))), new CompStatement(
                        new PrintStatement(new VariableEvalExpression("v")), new AssignmentStatement("v", new ArithmeticalExpression(new VariableEvalExpression("v"), ArithmeticalOperator.MINUS, new ValueEvalExpression(new IntValue(1))))
                )),
                new PrintStatement(new VariableEvalExpression("v"))
        ))));

        statements.add(new CompStatement(
                new VarDecStatement("v", new RefType(new IntType())),
                new CompStatement(
                        new HeapAllocationStatement("v", new ValueEvalExpression(new IntValue(20))),
                        new CompStatement(
                                new VarDecStatement("a", new RefType(new RefType(new IntType()))), // Declare a as Ref(Ref(int))
                                new CompStatement(
                                        new HeapAllocationStatement("a", new VariableEvalExpression("v")), // Allocate a with Ref(v)
                                        new CompStatement(
                                                new PrintStatement(new ReadHeapExpression(new VariableEvalExpression("v"))), // Print rH(v)
                                                new PrintStatement(new ArithmeticalExpression(new ReadHeapExpression(new ReadHeapExpression(new VariableEvalExpression("a"))), ArithmeticalOperator.PLUS, new ValueEvalExpression(new IntValue(5)))) // Print rH(rH(a)) + 5
                                        ))))));

        statements.add(new CompStatement(
                new VarDecStatement("v", new RefType(new IntType())),
                new CompStatement(
                        new HeapAllocationStatement("v", new ValueEvalExpression(new IntValue(20))),
                        new CompStatement(
                                new PrintStatement(new ReadHeapExpression(new VariableEvalExpression("v"))),
                                new CompStatement(
                                        new WriteHeapStatement("v", new ValueEvalExpression(new IntValue(5))),
                                        new PrintStatement(new ReadHeapExpression(new VariableEvalExpression("v")))
                                )
                        )
                )
        ));
        statements.add(new CompStatement(
                new VarDecStatement("v", new IntType()),
                new CompStatement(
                        new VarDecStatement("a", new RefType(new IntType())),
                        new CompStatement(
                                new AssignmentStatement("v", new ValueEvalExpression(new IntValue(10))),
                                new CompStatement(
                                        new HeapAllocationStatement("a", new ValueEvalExpression(new IntValue(22))),
                                        new CompStatement(
                                                new ForkStatement(
                                                        new CompStatement(
                                                                new WriteHeapStatement("a", new ValueEvalExpression(new IntValue(30))),
                                                                new CompStatement(
                                                                        new AssignmentStatement("v", new ValueEvalExpression(new IntValue(32))),
                                                                        new CompStatement(
                                                                                new PrintStatement(new VariableEvalExpression("v")),
                                                                                new PrintStatement(new ReadHeapExpression(new VariableEvalExpression("a")))
                                                                        )
                                                                )
                                                        )
                                                ), new CompStatement(
                                                new PrintStatement(new VariableEvalExpression("v")), new CompStatement(new PrintStatement(new ReadHeapExpression(new VariableEvalExpression("a"))), new PrintStatement(new VariableEvalExpression("v")))
                                        ))
                                )
                        )
                )
        ));

        statements.add(new CompStatement(
                new VarDecStatement("a", new IntType()),
                new CompStatement(
                        new VarDecStatement("b", new IntType()),
                        new CompStatement(
                                new VarDecStatement("c", new IntType()),
                                new CompStatement(
                                        new AssignmentStatement("a", new ValueEvalExpression(new IntValue(1))),
                                        new CompStatement(
                                                new AssignmentStatement("b", new ValueEvalExpression(new IntValue(2))),
                                                new CompStatement(
                                                        new AssignmentStatement("c", new ValueEvalExpression(new IntValue(5))),
                                                        new CompStatement(
                                                                new SwitchStatement(
                                                                        new ArithmeticalExpression(new VariableEvalExpression("a"), ArithmeticalOperator.MULTIPLY, new ValueEvalExpression(new IntValue(10))),
                                                                        new ArithmeticalExpression(new VariableEvalExpression("b"), ArithmeticalOperator.MULTIPLY, new VariableEvalExpression("c")),
                                                                        new CompStatement(
                                                                                new PrintStatement(new VariableEvalExpression("a")),
                                                                                new PrintStatement(new VariableEvalExpression("b"))
                                                                        ),
                                                                        new ValueEvalExpression(new IntValue(10)),
                                                                        new CompStatement(
                                                                                new PrintStatement(new ValueEvalExpression(new IntValue(100))),
                                                                                new PrintStatement(new ValueEvalExpression(new IntValue(200)))
                                                                        ),
                                                                        new PrintStatement(new ValueEvalExpression(new IntValue(300)))
                                                                ),
                                                                new PrintStatement(new ValueEvalExpression(new IntValue(300)))
                                                        )
                                                )
                                        )
                                )
                        )
                )
        ));

        // File: HelloApplication.java (partial)
        statements.add(new CompStatement(
                new VarDecStatement("v", new IntType()),
                new CompStatement(
                        new VarDecStatement("x", new IntType()),
                        new CompStatement(
                                new VarDecStatement("y", new IntType()),
                                new CompStatement(
                                        // Assignment to v=0
                                        new AssignmentStatement("v", new ValueEvalExpression(new IntValue(0))),
                                        new CompStatement(
                                                // Repeat...Until block
                                                new RepeatUntilStatement(
                                                        // Body of repeat: fork(...); v=v+1
                                                        new CompStatement(
                                                                new ForkStatement(
                                                                        // Child thread: print(v); v=v-1
                                                                        new CompStatement(
                                                                                new PrintStatement(new VariableEvalExpression("v")),
                                                                                new AssignmentStatement("v",
                                                                                        new ArithmeticalExpression(
                                                                                                new VariableEvalExpression("v"),
                                                                                                ArithmeticalOperator.MINUS,
                                                                                                new ValueEvalExpression(new IntValue(1))
                                                                                        )
                                                                                )
                                                                        )
                                                                ),
                                                                // Main thread: v=v+1
                                                                new AssignmentStatement("v",
                                                                        new ArithmeticalExpression(
                                                                                new VariableEvalExpression("v"),
                                                                                ArithmeticalOperator.PLUS,
                                                                                new ValueEvalExpression(new IntValue(1))
                                                                        )
                                                                )
                                                        ),
                                                        // Condition: until v==3
                                                        new RelationalExpression(
                                                                new VariableEvalExpression("v"),
                                                                RelationalOperator.EQUAL,
                                                                new ValueEvalExpression(new IntValue(3))
                                                        )
                                                ),
                                                // After loop: x=1; nop; y=3; nop; print(v*10)
                                                new CompStatement(
                                                        new AssignmentStatement("x", new ValueEvalExpression(new IntValue(1))),
                                                        new CompStatement(
                                                                new NopStatement(),
                                                                new CompStatement(
                                                                        new AssignmentStatement("y", new ValueEvalExpression(new IntValue(3))),
                                                                        new CompStatement(
                                                                                new NopStatement(),
                                                                                new PrintStatement(
                                                                                        new ArithmeticalExpression(
                                                                                                new VariableEvalExpression("v"),
                                                                                                ArithmeticalOperator.MULTIPLY,
                                                                                                new ValueEvalExpression(new IntValue(10))
                                                                                        )
                                                                                )
                                                                        )
                                                                )
                                                        )
                                                )
                                        )
                                )
                        )
                )
        ));

        statements.add(new CompStatement(
                new VarDecStatement("a", new RefType(new IntType())),
                new CompStatement(
                        new HeapAllocationStatement("a", new ValueEvalExpression(new IntValue(20))),
                        new CompStatement(
                                new ForStatement(
                                        "v",
                                        new ValueEvalExpression(new IntValue(0)),
                                        new ValueEvalExpression(new IntValue(3)),
                                        new ArithmeticalExpression(
                                                new VariableEvalExpression("v"),
                                                ArithmeticalOperator.PLUS,
                                                new ValueEvalExpression(new IntValue(1))
                                        ),
                                        new ForkStatement(
                                                new CompStatement(
                                                        new PrintStatement(new VariableEvalExpression("v")),
                                                        new AssignmentStatement(
                                                                "v",
                                                                new ArithmeticalExpression(
                                                                        new VariableEvalExpression("v"),
                                                                        ArithmeticalOperator.MULTIPLY,
                                                                        new ReadHeapExpression(new VariableEvalExpression("a"))
                                                                )
                                                        )
                                                )
                                        )
                                ),
                                new PrintStatement(new ReadHeapExpression(new VariableEvalExpression("a")))
                        )
                )
        ));

        statements.add(new CompStatement(new VarDecStatement("v1",new RefType(new IntType())),new CompStatement(new VarDecStatement("v2",new RefType(new IntType())),
                new CompStatement(new VarDecStatement("x",new IntType()),new CompStatement(new VarDecStatement("q",new IntType()),new CompStatement(new HeapAllocationStatement("v1",new ValueEvalExpression(new IntValue(20))),
                        new CompStatement(new HeapAllocationStatement("v2",new ValueEvalExpression(new IntValue(30))), new CompStatement(new NewLockStatement("x"),new CompStatement(new ForkStatement(
                                new CompStatement(new ForkStatement(new CompStatement(new LockStatement("x"),new CompStatement(new WriteHeapStatement("v1",new ArithmeticalExpression(new ReadHeapExpression(new VariableEvalExpression("v1")),
                                        ArithmeticalOperator.MINUS,new ValueEvalExpression(new IntValue(1)))),new UnlockStatement("x")))),new CompStatement(new WriteHeapStatement("v1",new ArithmeticalExpression(
                                        new ReadHeapExpression(new VariableEvalExpression("v1")),ArithmeticalOperator.MULTIPLY,new ValueEvalExpression(new IntValue(10)))),new UnlockStatement("x")))),new CompStatement(new NewLockStatement("q"),
                                new CompStatement(new ForkStatement(new CompStatement(new ForkStatement(new CompStatement(new LockStatement("q"),new CompStatement(new WriteHeapStatement("v2",new ArithmeticalExpression(new ReadHeapExpression(new VariableEvalExpression("v2")),
                                        ArithmeticalOperator.PLUS,new ValueEvalExpression(new IntValue(5)))),new UnlockStatement("q")))),new CompStatement(new LockStatement("q"),new CompStatement(new WriteHeapStatement("v2",new ArithmeticalExpression(new ReadHeapExpression(
                                        new VariableEvalExpression("v2")),ArithmeticalOperator.MULTIPLY,new ValueEvalExpression(new IntValue(10)))),new UnlockStatement("q"))))),new CompStatement(new NopStatement(),new CompStatement(new NopStatement(),new CompStatement(new NopStatement(),new CompStatement(new NopStatement(),
                                        new CompStatement(new LockStatement("x"),new CompStatement(new PrintStatement(new ReadHeapExpression(new VariableEvalExpression("v1"))),
                                                new CompStatement(new UnlockStatement("x"),new CompStatement(new LockStatement("q"),new CompStatement(new PrintStatement(new ReadHeapExpression(new VariableEvalExpression("v2"))),
                                                        new UnlockStatement("q")))))))))))))))))))));
        return statements;
    }

    private static List<IRepository> createRepositories(List<PrgState> programStates) {
        List<IRepository> repositories = new ArrayList<>();

        for (int i = 0; i < programStates.size(); i++) {
            IRepository repo = new Repository("logFile" + (i + 1) + ".txt");
            repo.add(programStates.get(i));
            repositories.add(repo);
        }

        return repositories;
    }

    private static List<ExecutorService> createExecutors(int count) {
        List<ExecutorService> executors = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            executors.add(Executors.newFixedThreadPool(2));
        }

        return executors;
    }

    private static List<Controller> createControllers(List<IRepository> repositories, List<ExecutorService> executors) {
        List<Controller> controllers = new ArrayList<>();

        for (int i = 0; i < repositories.size(); i++) {
            controllers.add(new Controller(repositories.get(i), executors.get(i)));
        }

        return controllers;
    }

    private static ObservableList<String> createProgramList(List<IStatement> statements) {
        ObservableList<String> programs = FXCollections.observableArrayList();

        for (IStatement stmt : statements) {
            programs.add(stmt.toString());
        }

        return programs;
    }


    public class LockEntry {
        private final SimpleIntegerProperty address;
        private final SimpleIntegerProperty value;

        public LockEntry(int address, int value) {
            this.address = new SimpleIntegerProperty(address);
            this.value = new SimpleIntegerProperty(value);
        }

        public int getAddress() { return address.get(); }
        public int getValue() { return value.get(); }

        public SimpleIntegerProperty addressProperty() { return address; }
        public SimpleIntegerProperty valueProperty() { return value; }
    }

    public static void main(String[] args) {
        launch();
    }
}
