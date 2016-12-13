package ru.spbau.kurbanov.vcs.commands;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import ru.spbau.kurbanov.vcs.repository.api.Command;
import ru.spbau.kurbanov.vcs.repository.api.Repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Parameters(commandDescription = "AddCommand file contents to the index")
public class AddCommand extends Command {

    @Parameter(description = "File patterns to add to the index")
    private final List<String> fileNames = new ArrayList<>();

    public AddCommand(Repository repo) {
        super(repo);
    }

    @Override
    public void execute() throws IOException {
        for (String fileName : fileNames) {
            repo.add(fileName);
        }
    }
}
