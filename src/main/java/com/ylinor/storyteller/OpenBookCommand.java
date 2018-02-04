package com.ylinor.storyteller;

import com.ylinor.storyteller.data.beans.dao.DialogDao;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;

import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;


import javax.inject.Inject;

public class OpenBookCommand implements CommandExecutor {

    @Inject
    private BookGenerator bookGenerator;
    @Inject
    private DialogDao dialogDao;


    public OpenBookCommand(){
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        int dialogId,pageId;
        if(args.getOne("dialog").isPresent())
            dialogId = args.<Integer>getOne("dialog").get();
        else {
            return CommandResult.empty();
        }
        if(args.getOne("page").isPresent())
            pageId = args.<Integer>getOne("page").get();
        else {
            return CommandResult.empty();
        }
        dialogDao.getDialog(dialogId);



        return CommandResult.success();
    }

}
