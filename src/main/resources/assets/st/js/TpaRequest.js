var Component = Java.type("net.minecraft.network.chat.Component");
var ChatFormatting = Java.type("net.minecraft.ChatFormatting");
var ClickEvent = Java.type("net.minecraft.network.chat.ClickEvent");
var Language = Java.type("dev.anye.mc.st.config.Language");


var msg = _tpa_send_player.getName().copy()
    .append(Language.getComponent("command.tpa.msg.tip").withStyle(ChatFormatting.GOLD))
    .append(Language.getComponent("command.tpa.msg.accept")
        .withStyle(function(style) {return style.withColor(ChatFormatting.GREEN).withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpaAccept"));}
        ));

player.sendSystemMessage(msg);