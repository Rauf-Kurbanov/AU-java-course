package ru.spbau.kurbanov.vcs.commands;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import lombok.RequiredArgsConstructor;
import ru.spbau.kurbanov.vcs.Repository;

@RequiredArgsConstructor
@Parameters(commandDescription = "Join two or more development histories together")
public class MergeCommand implements Command {

    private final Repository repo;

    @Parameter(description = "Name of the branch to merge to")
    private String branchName;

    @Override
    public void execute() {
        repo.merge(branchName);
    }
}
