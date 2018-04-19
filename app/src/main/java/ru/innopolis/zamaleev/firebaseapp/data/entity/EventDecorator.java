package ru.innopolis.zamaleev.firebaseapp.data.entity;

import ru.innopolis.zamaleev.firebaseapp.enums.MarkerEnum;

/**
 * Created by Ilgiz on 7/5/2017.
 */

public class EventDecorator extends EventMap {
    private MarkerEnum logic;

    public EventDecorator(EventMap eventMap, MarkerEnum logic) {
        super(eventMap);
        this.logic = logic;
    }

    public MarkerEnum getLogic() {
        return logic;
    }

    public void setLogic(MarkerEnum logic) {
        this.logic = logic;
    }
}
