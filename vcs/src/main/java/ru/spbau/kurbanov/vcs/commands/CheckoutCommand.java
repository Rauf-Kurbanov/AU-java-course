package ru.spbau.kurbanov.vcs.commands;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import ru.spbau.kurbanov.vcs.api.Command;
import ru.spbau.kurbanov.vcs.api.Repository;

@Parameters(commandDescription = "CheckoutCommand a branch or paths to the working tree")
public class CheckoutCommand extends Command {

    @Parameter(description = "Name of the branch to switch")
    private String branchName;

    public CheckoutCommand(Repository repo) {
        super(repo);
    }

    @Override
    public void execute() {
        repo.checkout(branchName);
    }
}
