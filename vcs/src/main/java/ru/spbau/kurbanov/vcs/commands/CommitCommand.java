package ru.spbau.kurbanov.vcs.commands;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import ru.spbau.kurbanov.vcs.api.Command;
import ru.spbau.kurbanov.vcs.api.Repository;

import java.io.IOException;

@Parameters(commandDescription = "Record changes to the repository")
public class CommitCommand extends Command {

    @Parameter(names = {"-m", "--message"})
    private String message;

    public CommitCommand(Repository repo) {
        super(repo);
    }

    @Override
    public void execute() {
        try {
            repo.commit(message);
//            TODO
        } catch (IOException ignored) {}
    }
}
