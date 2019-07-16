package inferno;

import io.anuke.arc.collection.Array;

public class Text{
    public static final Array<String> 
    
    start = Array.with(
    "{Lucine}{face:lucine frown}You're late."
    ),
    
    phase1 = Array.with(
    "{Asmus}{face:asmus lookdown}Haven't you had enough?",
    "{Lucine}{face:lucine frown}Not while I'm still breathing."
    ),

    phase2 = Array.with(
    "{Lucine}{face:lucine frown}This is what you wanted, right?",
    "{Asmus}{face:asmus lookleft}You're delusional.",
    "{Lucine}{face:lucine annoyed}Good one.",
    "{Lucine}{face:lucine smug}Hey, I'm not the one with a mask."
    ),

    phase3 = Array.with(
    "{Asmus}{face:asmus lookahead}Do I have to remind you that you tried to have me killed?",
    "{Lucine}{face:lucine smug}I'm still trying.",
    "{Asmus}{face:asmus lookleft}Go figure.",
    "{Asmus}{face:asmus lookahead}I'm just saying you might benefit from having some perspective.",
    "{Lucine}{face:lucine annoyed}Yeah?",
    "{Lucine}{face:lucine frustrated}Well, how's this for some perspective?"
    ),

    phase4 = Array.with(
    "{Asmus}{face:asmus lookdown}He's not coming back, Lucine.",
    "{Lucine}{face:lucine upset}Because you killed him.",
    "{Asmus}{face:asmus lookleft}Because you had him attack me. What were you expecting?",
    "{Asmus}{face:asmus lookahead}It's that kind of attitude that got you where you are now.",
    "{Lucine}{face:lucine frown}And what about your attitude?"
    ),

    phase5 = Array.with(
    "{Lucine}{face:lucine concerned}All your life, you've wanted to become someone you're not.",
    "{Lucine}{face:lucine concerned}You've been chasing after a crown that's too big for your head.",
    "{Lucine}{face:lucine concerned}You crave the throne your father sits on.",
    "{Asmus}{face:asmus lookblank}...",
    "{Lucine}{face:lucine smug}But we both know that already.",
    "{Lucine}{face:lucine sad}You've come to kill me, haven't you?"
    ),
    
    phase6 = Array.with(
    "{Lucine}{face:lucine frustrated}You want perspective? Fine.",
    "{Lucine}{face:lucine frown}You're a selfish, ignorant egoist.",
    "{Lucine}{face:lucine frown}Trust me, I know what that's like.",
    "{Lucine}{face:lucine upset}And if you think for a second I'll forgive you for what you've done, you're even worse than I am.",
    "{Lucine}{face:lucine anger}Go now, pray to whatever cursed God spawned you."
    ),
    
    phase7 = Array.with(
    "{Asmus}{face:asmus lookdown}I'm sorry.",
    "{Lucine}{face:lucine concerned}No... you're not.",
    "{Lucine}{face:lucine concerned}Hey...",
    "{Lucine}{face:lucine sad}Please...",
    "{Lucine}{face:lucine sad}Don't miss.",
    "{Asmus}{face:asmus lookblank}Would you say a final prayer to your God?",
    "{Lucine}{face:lucine smug}Which one?"
    );
}
