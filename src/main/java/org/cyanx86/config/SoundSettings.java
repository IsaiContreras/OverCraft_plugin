package org.cyanx86.config;

import org.bukkit.Instrument;
import org.bukkit.Note;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.cyanx86.utils.Defaults.RoundSounds;
import org.cyanx86.utils.Functions;

import java.util.Map;

public class SoundSettings {


    // -- [[ ATTRIBUTES ]] --

    // -- PUBLIC --

    // -- PRIVATE --
    private static SoundSettings instance;

    private Instrument countDownInstrument;
    private Note countDownTone;
    private Note startTone;

    private Sound timeRunningOutSound;
    private Sound finishSound;

    private  Sound orderEntrySound;
    private Sound deliveredOrderSound;
    private Sound lostOrderSound;

    // -- [[ METHODS ]] --

    // -- PUBLIC --
    public SoundSettings() {
        this.loadDefault();
    }

    public Instrument getCountDownInstrument() {
        return this.countDownInstrument;
    }
    public Note getCountDownTone() {
        return this.countDownTone;
    }
    public Note getStartTone() {
        return this.startTone;
    }

    public Sound getTimeRunningOutSound() {
        return this.timeRunningOutSound;
    }
    public Sound getFinishSound() {
        return this.finishSound;
    }

    public Sound getOrderEntrySound() {
        return this.orderEntrySound;
    }
    public Sound getDeliveredOrderSound() {
        return this.deliveredOrderSound;
    }
    public Sound getLostOrderSound() {
        return this.lostOrderSound;
    }

    // -- PROTECTED --
    protected void load(FileConfiguration config) {
        try { if (config.get("round-starting.countdown-instrument") != null)
            this.countDownInstrument = Instrument.valueOf(
                (String)config.get("round-starting.countdown-instrument")
            );
        } catch (ClassCastException ignored) { }
        try { if (config.get("round-starting.countdown-tone") != null)
            this.countDownTone = Functions.deserializeNote(
                (Map<String, Object>)config.get("round-starting.countdown-tone"));
        } catch (ClassCastException ignored) { }
        try { if (config.get("round-starting.start-tone") != null)
            this.startTone = Functions.deserializeNote(
                (Map<String, Object>)config.get("round-starting.start-tone")
            );
        } catch (ClassCastException ignored) { }

        try { if (config.get("round-timer.time-running-out-sound") != null)
            this.timeRunningOutSound = Sound.valueOf((String)config.get("round-timer.time-running-out-sound"));
        } catch (ClassCastException ignored) { }
        try { if (config.get("round-timer.finish-sound") != null)
            this.finishSound = Sound.valueOf((String)config.get("round-timer.finish-sound"));
        } catch (ClassCastException ignored) { }

        try { if (config.get("order-sounds.order-entry-sound") != null)
            this.orderEntrySound = Sound.valueOf((String)config.get("order-sounds.order-entry-sound"));
        } catch (ClassCastException ignored) { }
        try { if (config.get("order-sounds.delivered-order-sound") != null)
            this.deliveredOrderSound = Sound.valueOf((String)config.get("order-sounds.delivered-order-sound"));
        } catch (ClassCastException ignored) { }
        try { if (config.get("order-sounds.lost-order-sound") != null)
            this.lostOrderSound = Sound.valueOf((String)config.get("order-sounds.lost-order-sound"));
        } catch (ClassCastException ignored) { }
    }

    protected void save(FileConfiguration config) {
        config.set("round-starting.countdown-instrument", this.countDownInstrument.name());
        config.set("round-starting.countdown-tone", Functions.serializeNote(this.countDownTone));
        config.set("round-starting.start-tone", Functions.serializeNote(this.startTone));

        config.set("round-timer.time-running-out-sound", this.timeRunningOutSound.name());
        config.set("round-timer.finish-sound", this.finishSound.name());

        config.set("order-sounds.order-entry-sound", this.orderEntrySound.name());
        config.set("order-sounds.delivered-order-sound", this.deliveredOrderSound.name());
        config.set("order-sounds.lost-order-sound", this.lostOrderSound.name());
    }

    // -- PRIVATE --

    private void loadDefault() {
        // RoundStart sounds
        this.countDownInstrument = RoundSounds.countDownInstrument;
        this.countDownTone = RoundSounds.countDownTone;
        this.startTone = RoundSounds.startTone;
        // RoundTimer sounds
        this.timeRunningOutSound = RoundSounds.timeRunningOutSound;
        this.finishSound = RoundSounds.finishSound;
        // Order sounds
        this.orderEntrySound = RoundSounds.orderEntrySound;
        this.deliveredOrderSound = RoundSounds.deliveredOrderSound;
        this.lostOrderSound = RoundSounds.lostOrderSound;
    }

}
