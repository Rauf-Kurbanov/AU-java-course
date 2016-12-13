package ru.spbau.kurbanov.vcs.commands;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import lombok.RequiredArgsConstructor;
import ru.spbau.kurbanov.vcs.Repository;

import java.io.IOException;

@RequiredArgsConstructor
@Parameters(commandDescription = "Record changes to the repository")
public class CommitCommand implements Command {

    private final Repository repo;

    @Parameter(names = {"-m", "--message"})
    private String message;

    @Override
    public void execute() {
        try {
            repo.commit(message);
//            TODO
        } catch (IOException ignored) {}
    }
}
