package ru.spbau.kurbanov.vcs.commands;

import com.beust.jcommander.Parameters;
import ru.spbau.kurbanov.vcs.repository.api.Command;
import ru.spbau.kurbanov.vcs.repository.api.Repository;

import java.io.IOException;
@Parameters(commandDescription = "Remove untracked files from the working tree")

public class CleanCommand extends Command {

    public CleanCommand(Repository repo) {
        super(repo);
    }

    @Override
    public void execute() throws IOException {

    }
}
