package inferno.type;

public enum Direction{
    left(true), up(false), right(false), down(false);

    final boolean flipped;

    Direction(boolean flipped){
        this.flipped = flipped;
    }

    static Direction fromAngle(float angle){
        return angle < 90 || angle > 270 ? right : left;
        /*
        if(angle < 45 || angle >= 315){
            return right;
        }else if(angle >= 45 && angle < 135){
            return up;
        }else if(angle >= 135 && angle < 225){
            return left;
        }else{
            return down;
        }*/
    }
}
