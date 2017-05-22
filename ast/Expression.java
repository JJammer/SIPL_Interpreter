package ast;

public interface Expression extends Node{
    Object accept(Visitor visitor);
}
