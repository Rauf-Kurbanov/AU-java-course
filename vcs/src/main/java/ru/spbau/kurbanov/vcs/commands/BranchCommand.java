package ru.spbau.kurbanov.vcs.commands;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import ru.spbau.kurbanov.vcs.api.Command;
import ru.spbau.kurbanov.vcs.api.Repository;

@Parameters(commandDescription = "List, create, or delete branches")
public class BranchCommand extends Command {

    @Parameter(names = "-d", description = "Delete")
    private Boolean delete = false;

    @Parameter(description = "Name of the new branch")
    private String branchName;

    public BranchCommand(Repository repo) {
        super(repo);
    }

    @Override
    public void execute() {
        if (delete) {
            repo.removeBranch(branchName);
        } else {
            repo.createBranch(branchName);
        }
    }
}
