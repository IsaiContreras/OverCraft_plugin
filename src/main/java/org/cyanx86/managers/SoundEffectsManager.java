package org.cyanx86.managers;

import org.bukkit.Instrument;
import org.bukkit.Note;
import org.bukkit.Sound;
import org.cyanx86.config.SoundSettings;

public class SoundEffectsManager {

    // -- [[ ATTRIBUTES ]] --

    // -- PUBLIC --

    // -- PRIVATE --
    private final GamePlayersManager playersManager;

    private final Instrument countDownInstrument;
    private final Note countDownTone;
    private final Note startTone;

    private final Sound timeRunningOutSound;
    private final Sound finishSound;

    private final Sound orderEntrySound;
    private final Sound deliveredOrderSound;
    private final Sound lostOrderSound;

    // -- [[ METHODS ]] --

    // -- PUBLIC --
    public SoundEffectsManager(GamePlayersManager playersManager) {
        this.playersManager = playersManager;

        SoundSettings settings = SoundSettings.getInstance();
        this.countDownInstrument = settings.getCountDownInstrument();
        this.countDownTone = settings.getCountDownTone();
        this.startTone = settings.getStartTone();

        this.timeRunningOutSound = settings.getTimeRunningOutSound();
        this.finishSound = settings.getFinishSound();

        this.orderEntrySound = settings.getOrderEntrySound();
        this.deliveredOrderSound = settings.getDeliveredOrderSound();
        this.lostOrderSound = settings.getLostOrderSound();
    }

    public void playCountDownNote() {
        this.playersManager.sendNoteToPlayer(
            countDownInstrument,
            countDownTone
        );
    }

    public void playStartRound() {
        this.playersManager.sendNoteToPlayer(
            countDownInstrument,
            startTone
        );
    }

    public void playTimeRunningOut() {
        this.playersManager.sendSoundToPlayer(
            this.timeRunningOutSound,
            1f, 1f
        );
    }

    public void playFinish() {
        this.playersManager.sendSoundToPlayer(
            this.finishSound,
            1f, 1f
        );
    }

    public void playOrderEntry() {
        this.playersManager.sendSoundToPlayer(
            this.orderEntrySound,
            1f, 1f
        );
    }

    public void playDeliveredOrder() {
        this.playersManager.sendSoundToPlayer(
            this.deliveredOrderSound,
            1f, 1f
        );
    }

    public void playLostOrder() {
        this.playersManager.sendSoundToPlayer(
            this.lostOrderSound,
            1f, 1f
        );
    }

    // -- PRIVATE --

}
