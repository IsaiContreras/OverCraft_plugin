package org.cyanx86.config;

import org.bukkit.Instrument;
import org.bukkit.Note;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.cyanx86.utils.CustomConfigFile;
import org.cyanx86.utils.Defaults.RoundSounds;
import org.cyanx86.utils.Functions;

import java.util.Map;

public class SoundSettings extends CustomConfigFile {


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
    public static SoundSettings getInstance() {
        if (instance == null)
            instance = new SoundSettings();
        return instance;
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
    @Override
    protected void load() {
        FileConfiguration config = this.getConfig();

        try { if (config.get("round_starting.countdown_instrument") != null)
            this.countDownInstrument = Instrument.valueOf((String)config.get("round_starting.countdown_instrument"));
        } catch (ClassCastException ignored) { }
        try { if (config.get("round_starting.countdown_tone") != null)
            this.countDownTone = Functions.deserializeNote(
                    (Map<String, Object>)config.get("round_starting.countdown_tone"));
        } catch (ClassCastException ignored) { }
        try { if (config.get("round_starting.start_tone") != null)
            this.startTone = Functions.deserializeNote((Map<String, Object>)config.get("round_starting.start_tone"));
        } catch (ClassCastException ignored) { }

        try { if (config.get("round_timer.time_running_out_sound") != null)
            this.timeRunningOutSound = Sound.valueOf((String)config.get("round_timer.time_running_out_sound"));
        } catch (ClassCastException ignored) { }
        try { if (config.get("round_timer.finish_sound") != null)
            this.finishSound = Sound.valueOf((String)config.get("round_timer.finish_sound"));
        } catch (ClassCastException ignored) { }

        try { if (config.get("order_sounds.order_entry_sound") != null)
            this.orderEntrySound = Sound.valueOf((String)config.get("order_sounds.order_entry_sound"));
        } catch (ClassCastException ignored) { }
        try { if (config.get("order_sounds.delivered_order_sound") != null)
            this.deliveredOrderSound = Sound.valueOf((String)config.get("order_sounds.delivered_order_sound"));
        } catch (ClassCastException ignored) { }
        try { if (config.get("order_sounds.lost_order_sound") != null)
            this.lostOrderSound = Sound.valueOf((String)config.get("order_sounds.lost_order_sound"));
        } catch (ClassCastException ignored) { }
    }

    @Override
    protected void reload() {
        this.reloadConfig();
        this.load();
    }

    @Override
    protected void save() {
        FileConfiguration config = this.getConfig();

        config.set("round_starting.countdown_instrument", this.countDownInstrument.name());
        config.set("round_starting.countdown_tone", Functions.serializeNote(this.countDownTone));
        config.set("round_starting.start_tone", Functions.serializeNote(this.startTone));

        config.set("round_timer.time_running_out_sound", this.timeRunningOutSound.name());
        config.set("round_timer.finish_sound", this.finishSound.name());

        config.set("order_sounds.order_entry_sound", this.orderEntrySound.name());
        config.set("order_sounds.delivered_order_sound", this.deliveredOrderSound.name());
        config.set("order_sounds.lost_order_sound", this.lostOrderSound.name());

        this.saveConfig();
    }

    // -- PRIVATE --
    private SoundSettings() {
        super(
            "soundsettings.yml",
            "ocf_settings",
            true
        );
        this.loadDefault();
        if (this.registerConfig())
            this.load();
        else this.save();
    }

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
