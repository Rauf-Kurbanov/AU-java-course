package ru.spbau.kurbanov.vcs.commands;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import lombok.RequiredArgsConstructor;
import ru.spbau.kurbanov.vcs.Repository;

@RequiredArgsConstructor
@Parameters(commandDescription = "CheckoutCommand a branch or paths to the working tree")
public class CheckoutCommand implements Command {

    private final Repository repo;

    @Parameter(description = "Name of the branch to switch")
    private String branchName;

    @Override
    public void execute() {
        repo.checkout(branchName);
    }
}
