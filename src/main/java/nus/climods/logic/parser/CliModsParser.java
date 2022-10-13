package nus.climods.logic.parser;

import static nus.climods.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static nus.climods.commons.core.Messages.MESSAGE_UNKNOWN_COMMAND;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nus.climods.logic.commands.Command;
import nus.climods.logic.commands.ListCommand;
import nus.climods.logic.commands.FindCommand;
import nus.climods.logic.parser.exceptions.ParseException;
import nus.climods.logic.parser.parameters.FacultyCodeParameter;
import nus.climods.logic.parser.parameters.UserFlagParameter;

/**
 * Parses user input.
 */
public class CliModsParser {

    /**
     * Used for initial separation of command word and args.
     */
    private static final Pattern BASIC_COMMAND_FORMAT = Pattern.compile("(?<commandWord>\\S+)(?<arguments>.*)");

    /**
     * Parses user input into command for execution.
     *
     * @param userInput full user input string
     * @return the command based on the user input
     * @throws ParseException if the user input does not conform the expected format
     */
    public static Command parseCommand(String userInput) throws ParseException {
        final Matcher matcher = BASIC_COMMAND_FORMAT.matcher(userInput.trim());
        if (!matcher.matches()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, "Invalid command"));
        }

        final String commandWord = matcher.group("commandWord");
        final String arguments = matcher.group("arguments");

        switch (commandWord) {
        case ListCommand.COMMAND_WORD:
            String trimmedArgs = arguments.trim();
            return new ListCommand(new FacultyCodeParameter(trimmedArgs), new UserFlagParameter(trimmedArgs));

        case (FindCommand.COMMAND_WORD):
            return new FindCommandParser().parse(arguments);

        default:
            throw new ParseException(MESSAGE_UNKNOWN_COMMAND);
        }
    }
}