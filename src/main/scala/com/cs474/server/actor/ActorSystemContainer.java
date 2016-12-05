package com.cs474.server.actor;

import akka.actor.ActorSystem;
import akka.stream.ActorMaterializer;

/**
 * Singleton class for the actor system and materializer
 */
public class ActorSystemContainer {

    private ActorSystem sys;
    private ActorMaterializer mat;

    // Constructor
    private ActorSystemContainer() {
        sys = ActorSystem.create("data-streaming");
        mat = ActorMaterializer.create(sys);
    }

    /**
     *  Retrieves the actor system
     *
     * @return // actor system
     */
    public ActorSystem getSystem() {
        return sys;
    }

    /**
     *  Retrieves actor materializer
     *
     * @return // actor materializer
     */
    public ActorMaterializer getMaterializer() {
        return mat;
    }

    // Class instance
    private static ActorSystemContainer instance = null;

    /**
     *  Retrieves the class instance
     *
     * @return // class instance
     */
    public static synchronized ActorSystemContainer getInstance() {
        if (instance == null) {
            instance = new ActorSystemContainer();
        }
        return instance;
    }
}
