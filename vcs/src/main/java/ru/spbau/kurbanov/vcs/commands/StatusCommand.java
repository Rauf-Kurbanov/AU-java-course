package ru.spbau.kurbanov.vcs.commands;

import com.beust.jcommander.Parameters;
import ru.spbau.kurbanov.vcs.repository.api.Command;
import ru.spbau.kurbanov.vcs.repository.api.Repository;

import java.io.IOException;

@Parameters(commandDescription = " Show the working tree status")
public class StatusCommand extends Command {
    public StatusCommand(Repository repo) {
        super(repo);
    }

    @Override
    public void execute() throws IOException {

    }
}
