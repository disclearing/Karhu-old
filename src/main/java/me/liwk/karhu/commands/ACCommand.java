package me.liwk.karhu.commands;

import me.liwk.karhu.Karhu;

public class ACCommand {

    public Karhu main = Karhu.getInstance();

    public ACCommand() {
        main.getFramework().registerCommands(this);
    }
}
