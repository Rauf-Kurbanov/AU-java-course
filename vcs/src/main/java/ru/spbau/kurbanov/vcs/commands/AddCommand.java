package ru.spbau.kurbanov.vcs.commands;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import lombok.RequiredArgsConstructor;
import ru.spbau.kurbanov.vcs.Repository;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Parameters(commandDescription = "AddCommand file contents to the index")
public class AddCommand implements Command {

    private final Repository repository;

    @Parameter(description = "File patterns to add to the index")
    private final List<String> fileNames = new ArrayList<>();

    @Override
    public void execute() {
        for (String fileName : fileNames) {
            // TODO do I need Files here?
            File inpFile = new File(repository.getPwd(), fileName);
            if (!inpFile.exists()) {
                System.out.format("fatal: pathspec '%s' did not match any files", fileName);
                return;
            }
            repository.add(inpFile);
        }
        System.out.println("Running add");
    }
}
