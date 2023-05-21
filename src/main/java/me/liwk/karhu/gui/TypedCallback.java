package me.liwk.karhu.gui;

@FunctionalInterface
public interface TypedCallback<T> {

    public void execute(T type);

}

