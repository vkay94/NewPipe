package org.schabi.newpipe.util.stream_dialog;

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

    // TODO: Above start from here: single stream

    // TODO: Start from here in background (append queue to existing)
    // TODO: Start from here in popup (append queue to existing)
    // TODO: Enqueue from here (append queue to existing)

    start_here_on_background_queue(R.string.start_here_on_background_queue, (fragment, item) ->
            NavigationHelper.playOnBackgroundPlayer(fragment.getContext(),
                    new SinglePlayQueue(item), true)),

    start_here_on_popup_queue(R.string.start_here_on_popup_queue, (fragment, item) ->
            NavigationHelper.playOnPopupPlayer(fragment.getContext(),
                    new SinglePlayQueue(item), true)),

    // TODO: Creates Queue if not exist, otherwise append
    append_from_here(R.string.append_from_here, (fragment, item) ->
            NavigationHelper.playOnBackgroundPlayer(fragment.getContext(),
                    new SinglePlayQueue(item), true)),



    /**
     * <p>
     *     Represents a group of actions.
     * </p>
     *
     * You'll have to set the values (resource and actions list) after instantiating this entry
     * because enums in Java don't allow constructor instantiating.
     */
    group(-1, new ArrayList<>()),

    custom(-1, (fragment, item) -> { /*no-op*/ });

    ///////////////
    // variables //
    ///////////////

    private int resource;
    private final StreamDialogEntryAction defaultAction;
    private StreamDialogEntryAction customAction;

    private List<StreamDialogEntry> subActions = Collections.emptyList();

    StreamDialogEntry(final int resource, final StreamDialogEntryAction defaultAction) {
        this.resource = resource;
        this.defaultAction = defaultAction;
        this.customAction = null;
    }

    // Pass it as is
    StreamDialogEntry(final int resource, final ArrayList<StreamDialogEntry> subActions) {
        this.resource = resource;
        this.subActions = subActions;
        this.defaultAction = null;
        this.customAction = null;
    }

    public int getResource() {
        return resource;
    }

    public void setResource(final int resource) {
        this.resource = resource;
    }

    public void setSubActions(final List<StreamDialogEntry> actions) {
        this.subActions = actions;
    }

    public List<StreamDialogEntry> getSubActions() {
        return subActions;
    }

    public boolean hasSubEntries() {
        return subActions.size() > 0;
    }

    public StreamDialogEntryAction getAction() {
        if (customAction != null) {
            return customAction;
        } else  {
            return defaultAction;
        }
    }

    /**
     * Sets a custom {@link StreamDialogEntryAction}.
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
