package ru.spbau.kurbanov.vcs.commands;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import lombok.RequiredArgsConstructor;
import ru.spbau.kurbanov.vcs.Repository;

@RequiredArgsConstructor
@Parameters(commandDescription = "List, create, or delete branches")
public class BranchCommand implements Command {

    private final Repository repo;

    @Parameter(names = "-d", description = "Delete")
    private Boolean delete = false;

    @Parameter(description = "Name of the new branch")
    private String branchName;

    @Override
    public void execute() {
        if (delete) {
            repo.removeBranch(branchName);
        } else {
            repo.createBranch(branchName);
        }
    }
}
