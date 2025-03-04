package controller;

import model.ADT.MyIHeap;
import model.ADT.MyIMap;
import model.ADT.MyMap;
import model.types.RefType;
import model.values.IValue;
import model.values.RefValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class GarbageCollector {
    private List<MyIMap<String, IValue>> symtables;
    private MyIHeap heap;
    public GarbageCollector(List<MyIMap<String, IValue>> symtables, MyIHeap heap) {
        this.symtables = symtables;
        this.heap = heap;
    }
    MyIMap<Integer,IValue> safeGarbageCollector(List<Integer> symTableAddresses){
        ConcurrentHashMap<Integer,IValue> newHeap = new ConcurrentHashMap<>();
        heap.getHeap().getKeys().stream()
                .filter(e->symTableAddresses.contains(e))
                        .forEach(e->newHeap.put(e, heap.getHeap().getValue(e)));
        return new MyMap(newHeap);
    }

    List<Integer> getAddressesFromSymTable(){
//        return this.symtable.getKeys().stream().map(elem->symtable.getValue(elem)).flatMap(symTableValue->{
//            List<Integer> symTableAddresses = new ArrayList<>();
//            if (symTableValue instanceof RefValue) {
//                symTableAddresses.add(((RefValue) symTableValue).getAddress());
//                if (((RefValue) symTableValue).getLocationType() instanceof RefType)
//                {
//                    RefValue refValue = (RefValue) symTableValue;
//                    while (refValue.getLocationType() instanceof RefType && this.heap.containsKey(refValue.getAddress()))
//                    {
//                        refValue = (RefValue) this.heap.getValue(refValue.getAddress());
//                        symTableAddresses.add(refValue.getAddress());
//                    }
//                }
//            }
//            return symTableAddresses.stream();
//        }).collect(Collectors.toList());
        return this.symtables.stream().flatMap(symtable->symtable.getKeys().stream()
                .map(elem->symtable.getValue(elem))).collect(Collectors.toList()).stream()
                .flatMap(symTableValue->{
                    List<Integer> symTableAddresses = new ArrayList<>();
                    if (symTableValue instanceof RefValue) {
                        symTableAddresses.add(((RefValue) symTableValue).getAddress());
                        if (((RefValue) symTableValue).getLocationType() instanceof RefType)
                        {
                            RefValue refValue = (RefValue) symTableValue;
                            while (refValue.getLocationType() instanceof RefType && this.heap.containsKey(refValue.getAddress()))
                            {
                                refValue = (RefValue) this.heap.getValue(refValue.getAddress());
                                symTableAddresses.add(refValue.getAddress());
                            }
                        }
                    }
                    return symTableAddresses.stream();
                }).collect(Collectors.toList());
    }
}