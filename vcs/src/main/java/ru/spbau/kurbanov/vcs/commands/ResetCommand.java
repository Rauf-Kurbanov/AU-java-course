package ru.spbau.kurbanov.vcs.commands;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import ru.spbau.kurbanov.vcs.repository.api.Command;
import ru.spbau.kurbanov.vcs.repository.api.Repository;

import java.io.IOException;
import java.util.List;

@Parameters(commandDescription = "Reset files from the repo")
public class ResetCommand extends Command {

    @Parameter(description = "Files to reset")
    private List<String> files;

    public ResetCommand(Repository repo) {
        super(repo);
    }

    @Override
    public void execute() throws IOException {

    }
}
