package ru.spbau.kurbanov.vcs.commands;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import ru.spbau.kurbanov.vcs.repository.api.Command;
import ru.spbau.kurbanov.vcs.repository.api.Repository;

import java.io.IOException;
import java.util.List;

@Parameters(commandDescription = "Rm files from the repo")
public class RmCommand extends Command {

    @Parameter(description = "Files to remove")
    private List<String> fileNames;

    public RmCommand(Repository repo) {
        super(repo);
    }

    @Override
    public void execute() throws IOException {

    }
}
