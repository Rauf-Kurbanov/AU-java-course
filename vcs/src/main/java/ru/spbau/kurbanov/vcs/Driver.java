package ru.spbau.kurbanov.vcs;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.MissingCommandException;
import ru.spbau.kurbanov.vcs.commands.*;
import ru.spbau.kurbanov.vcs.repository.api.Command;
import ru.spbau.kurbanov.vcs.repository.api.Repository;
import ru.spbau.kurbanov.vcs.repository.api.RepositoryFactory;
import ru.spbau.kurbanov.vcs.repository.impl.RepositorySerializer;

import java.io.File;
import java.io.IOException;

public class Driver {

    private static final File projRoot = new File("/home/rauf/Programs/semester_3/java/AU-java-course/vcs/testDir");
    private static final Repository repo = RepositoryFactory.initRepo(projRoot);
    private static final JCommander commandParser = new JCommander();

    static {
        commandParser.addCommand("add", new AddCommand(repo));
        commandParser.addCommand("commit", new CommitCommand(repo));
        commandParser.addCommand("branch", new BranchCommand(repo));
        commandParser.addCommand("checkout", new CheckoutCommand(repo));
        commandParser.addCommand("log", new LogCommand(repo));
        commandParser.addCommand("merge", new MergeCommand(repo));
    }

    public static void main(String[] args) throws IOException {

        String[] testArgs = {"commit", "-m", "asdda"};
        args = testArgs;

        try {
            commandParser.parse(args);
        } catch (MissingCommandException e) {
            System.out.println("Invalid command");
            commandParser.usage();
            return;
        }

        if (args.length == 0) {
            commandParser.usage();
            return;
        }

        String parsedCommand = commandParser.getParsedCommand();
        JCommander parsedJCommander = commandParser.getCommands().get(parsedCommand);
        Command commandObject = (Command) parsedJCommander.getObjects().get(0);

        try {
            commandObject.execute();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        RepositorySerializer.serialize(repo);
    }
}