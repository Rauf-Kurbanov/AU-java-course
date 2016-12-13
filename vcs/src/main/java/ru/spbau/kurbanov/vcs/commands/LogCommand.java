package ru.spbau.kurbanov.vcs.commands;

import com.beust.jcommander.Parameters;
import lombok.RequiredArgsConstructor;
import ru.spbau.kurbanov.vcs.Repository;

@RequiredArgsConstructor
@Parameters(commandDescription = "Show commit logs")
public class LogCommand implements Command {

    private final Repository repo;

    @Override
    public void execute() {
        repo.log();
    }
}
