from rlbot.agents.executable_with_socket_agent import ExecutableWithSocketAgent

class JavaExample(ExecutableWithSocketAgent):
    def get_port(self) -> int:
        return 17357
