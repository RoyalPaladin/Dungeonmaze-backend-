public class StatePattern {
    public interface State {
        public void stateAction(StateObject obj);
    }
      
    public interface StateObject {
        public void setState(State state);
        public State getState();
    }
      
    public class PlayState implements State {
        @Override
        public void stateAction(StateObject obj) { 
            System.out.println("Playing...");
            obj.setState(this);
        }
    }
      
    public class PausedState implements State {
        @Override
        public void stateAction(StateObject obj) {
            System.out.println("Pausing...");
            obj.setState(this);  
        }
    }
      
    public class VideoPlayer implements StateObject {
        private State state;
      
        public VideoPlayer(State startingState) {
            state = startingState;
        }
      
        @Override
        public void setState(State state) { this.state = state; }
        @Override
        public State getState() { return state; }
    }
      
    public static void main(String[] args) {
        StatePattern sp = new StatePattern();
        VideoPlayer videoPlayer = sp.new VideoPlayer(sp.new PausedState());
        PlayState play = sp.new PlayState();
        PausedState pause = sp.new PausedState();
        play.stateAction(videoPlayer); // Prints "Playing..."
        pause.stateAction(videoPlayer); // Print "Pausing..."
        videoPlayer.getState(); // Returns type PausedState
    } 
}
