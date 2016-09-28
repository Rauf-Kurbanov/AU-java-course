package ru.spbau.kurbanov.vcs.commands;

import com.beust.jcommander.Parameters;
import ru.spbau.kurbanov.vcs.api.Command;
import ru.spbau.kurbanov.vcs.api.Repository;

@Parameters(commandDescription = "Show commit logs")
public class LogCommand extends Command {

    public LogCommand(Repository repo) {
        super(repo);
    }

    @Override
    public void execute() {
        repo.log();
    }
}
