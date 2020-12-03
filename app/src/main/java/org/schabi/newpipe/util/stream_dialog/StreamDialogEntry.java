package org.schabi.newpipe.util.stream_dialog;

import android.content.Context;

import androidx.fragment.app.Fragment;

import org.schabi.newpipe.R;
import org.schabi.newpipe.extractor.stream.StreamInfoItem;
import org.schabi.newpipe.local.dialog.PlaylistAppendDialog;
import org.schabi.newpipe.local.dialog.PlaylistCreationDialog;
import org.schabi.newpipe.player.MainPlayer;
import org.schabi.newpipe.player.helper.PlayerHolder;
import org.schabi.newpipe.player.playqueue.SinglePlayQueue;
import org.schabi.newpipe.util.NavigationHelper;
import org.schabi.newpipe.util.ShareUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.schabi.newpipe.player.MainPlayer.PlayerType.AUDIO;
import static org.schabi.newpipe.player.MainPlayer.PlayerType.POPUP;

public enum StreamDialogEntry {
    //////////////////////////////////////
    // enum values with DEFAULT actions //
    //////////////////////////////////////

    /**
     * Enqueues the stream automatically to the current PlayerType.<br>
     * <br>
     * Info: Add this entry within showStreamDialog.
     */
    enqueue(R.string.enqueue_stream, (fragment, item) -> {
        final MainPlayer.PlayerType type = PlayerHolder.getType();

        if (type == AUDIO) {
            NavigationHelper.enqueueOnBackgroundPlayer(fragment.getContext(),
                    new SinglePlayQueue(item), false);
        } else if (type == POPUP) {
            NavigationHelper.enqueueOnPopupPlayer(fragment.getContext(),
                    new SinglePlayQueue(item), false);
        } else /* type == VIDEO */ {
            NavigationHelper.enqueueOnVideoPlayer(fragment.getContext(),
                    new SinglePlayQueue(item), false);
        }
    }),

    start_here_on_background(R.string.start_here_on_background, (fragment, item) ->
            NavigationHelper.playOnBackgroundPlayer(fragment.getContext(),
                    new SinglePlayQueue(item), true)),

    start_here_on_popup(R.string.start_here_on_popup, (fragment, item) ->
            NavigationHelper.playOnPopupPlayer(fragment.getContext(),
                    new SinglePlayQueue(item), true)),

    set_as_playlist_thumbnail(R.string.set_as_playlist_thumbnail, (fragment, item) -> {
    }), // has to be set manually

    delete(R.string.delete, (fragment, item) -> {
    }), // has to be set manually

    append_playlist(R.string.append_playlist, (fragment, item) -> {
        if (fragment.getFragmentManager() != null) {
            final PlaylistAppendDialog d = PlaylistAppendDialog
                    .fromStreamInfoItems(Collections.singletonList(item));

            PlaylistAppendDialog.onPlaylistFound(fragment.getContext(),
                () -> d.show(fragment.getFragmentManager(), "StreamDialogEntry@append_playlist"),
                () -> PlaylistCreationDialog.newInstance(d)
                        .show(fragment.getFragmentManager(), "StreamDialogEntry@create_playlist")
            );
        }
    }),

    share(R.string.share, (fragment, item) ->
            ShareUtils.shareUrl(fragment.getContext(), item.getName(), item.getUrl())),

    groupEntry(R.string.tracks, new ArrayList<>());

    ///////////////
    // variables //
    ///////////////

    private static StreamDialogEntry[] enabledEntries;
    private int resource;
    private final StreamDialogEntryAction defaultAction;
    private StreamDialogEntryAction customAction;

    private List<StreamDialogEntry> subEntries = Collections.emptyList();

    StreamDialogEntry(final int resource, final StreamDialogEntryAction defaultAction) {
        this.resource = resource;
        this.defaultAction = defaultAction;
        this.customAction = null;
    }

    // Pass it as is
    StreamDialogEntry(final int resource, final ArrayList<StreamDialogEntry> subEntries) {
        this.resource = resource;
        this.subEntries = subEntries;
        this.defaultAction = null;
        this.customAction = null;
    }

    public int getResource() {
        return resource;
    }

    public void setResource(final int resource) {
        this.resource = resource;
    }

    public void setSubEntries(final List<StreamDialogEntry> entries) {
        this.subEntries = entries;
    }

    public List<StreamDialogEntry> getSubEntries() {
        return subEntries;
    }

    public boolean hasSubEntries() {
        return subEntries.size() > 0;
    }

    public StreamDialogEntryAction getAction() {
        if (customAction != null) {
            return customAction;
        } else  {
            return defaultAction;
        }
    }

    ///////////////////////////////////////////////////////
    // non-static methods to initialize and edit entries //
    ///////////////////////////////////////////////////////

    public static void setEnabledEntries(final List<StreamDialogEntry> entries) {
        setEnabledEntries(entries.toArray(new StreamDialogEntry[0]));
    }

    /**
     * To be called before using {@link #setCustomAction(StreamDialogEntryAction)}.
     *
     * @param entries the entries to be enabled
     */
    public static void setEnabledEntries(final StreamDialogEntry... entries) {
        // cleanup from last time StreamDialogEntry was used
        for (final StreamDialogEntry streamDialogEntry : values()) {
            streamDialogEntry.customAction = null;
        }

        enabledEntries = entries;
    }

    public static String[] getCommands(final Context context) {
        final String[] commands = new String[enabledEntries.length];
        for (int i = 0; i != enabledEntries.length; ++i) {
            commands[i] = context.getResources().getString(enabledEntries[i].resource);
        }

        return commands;
    }


    ////////////////////////////////////////////////
    // static methods that act on enabled entries //
    ////////////////////////////////////////////////

    public static void clickOn(final int which, final Fragment fragment,
                               final StreamInfoItem infoItem) {
        if (enabledEntries[which].customAction == null) {
            enabledEntries[which].defaultAction.onClick(fragment, infoItem);
        } else {
            enabledEntries[which].customAction.onClick(fragment, infoItem);
        }
    }

    /**
     * Can be used after {@link #setEnabledEntries(StreamDialogEntry...)} has been called.
     *
     * @param action the action to be set
     */
    public void setCustomAction(final StreamDialogEntryAction action) {
        this.customAction = action;
    }

    public interface StreamDialogEntryAction {
        void onClick(Fragment fragment, StreamInfoItem infoItem);
    }
}
