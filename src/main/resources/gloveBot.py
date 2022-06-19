from rlbot.agents.executable_with_socket_agent import ExecutableWithSocketAgent

# Is called by the rlbot framework to handle communication with our bot
class GloveBotModule(ExecutableWithSocketAgent):
    def get_port(self) -> int:
        return 17357
