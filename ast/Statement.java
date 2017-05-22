package ast;

public interface Statement extends Node{
    void accept(Visitor visitor);
}
