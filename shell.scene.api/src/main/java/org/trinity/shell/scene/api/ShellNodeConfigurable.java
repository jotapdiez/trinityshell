package org.trinity.shell.scene.api;

import javax.annotation.Nonnull;

/**
 *
 */
public interface ShellNodeConfigurable {

    void configure(int x, int y, int width, int height);

    void attachDisplayBuffer(@Nonnull Object buffer);

    void setParent(ShellNode shellNodeParent);

    void markDestroyed();

    void setVisible(Boolean visible);
}
