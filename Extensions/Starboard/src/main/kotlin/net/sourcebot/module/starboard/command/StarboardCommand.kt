package net.sourcebot.module.starboard.command

import net.dv8tion.jda.api.entities.Message
import net.sourcebot.api.response.Response
import net.sourcebot.api.response.InfoResponse
import net.sourcebot.api.response.SuccessResponse
import net.sourcebot.api.command.Command
import net.sourcebot.api.command.RootCommand
import net.sourcebot.api.command.argument.Adapter
import net.sourcebot.api.command.argument.Argument
import net.sourcebot.api.command.argument.ArgumentInfo
import net.sourcebot.api.command.argument.Arguments
import net.sourcebot.module.starboard.misc.StarboardDataManager

class StarboardCommand(
    private val dataManager: StarboardDataManager
) : RootCommand() {
    override val name = "starboard"
    override val description = "Configure the Starboard"
    override val guildOnly = true
    override val permission = name

    private inner class StarboardThresholdCommand : CommandBootstrap(
        "threshold", "Set the Starboard Star threshold."
    ) {
        override val argumentInfo = ArgumentInfo(
            Argument("threshold", "The desired star threshold")
        )

        override fun execute(message: Message, args: Arguments): Response {
            val threshold = args.next(Adapter.int(), "You did not specify a star threshold!")
            val data = dataManager[message.guild]
            if (data.threshold == threshold) return InfoResponse(
                "Star Threshold", "The star threshold is already set to `$threshold`!"
            )
            data.threshold = threshold
            dataManager.save(message.guild, data)
            return SuccessResponse(
                "Star Threshold", "The star threshold has been set to `$threshold`!"
            )
        }
    }

    private inner class StarboardChannelCommand : CommandBootstrap(
        "channel", "Set the Starboard channel."
    ) {
        override fun execute(message: Message, args: Arguments): Response {
            val data = dataManager[message.guild]
            if (data.channel == message.channel.id) return InfoResponse(
                "Starboard Channel", "This channel is already the Starboard channel!"
            )
            data.channel = message.channel.id
            dataManager.save(message.guild, data)
            return SuccessResponse(
                "Starboard Channel", "This channel is now the Starboard channel!"
            )
        }
    }

    init {
        addChildren(
            StarboardThresholdCommand(),
            StarboardChannelCommand()
        )
    }
}

abstract class CommandBootstrap(
    override val name: String,
    override val description: String
) : Command() {
    override val permission by lazy { "starboard.$name" }
    override val guildOnly = true
}

