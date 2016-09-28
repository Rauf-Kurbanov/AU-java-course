package ru.spbau.kurbanov.vcs.commands;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import ru.spbau.kurbanov.vcs.api.Command;
import ru.spbau.kurbanov.vcs.api.Repository;

@Parameters(commandDescription = "Join two or more development histories together")
public class MergeCommand extends Command {

    @Parameter(description = "Name of the branch to merge to")
    private String branchName;

    public MergeCommand(Repository repo) {
        super(repo);
    }

    @Override
    public void execute() {
        repo.merge(branchName);
    }
}
