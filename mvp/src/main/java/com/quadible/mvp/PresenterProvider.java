/**
 * Copyright 2017 Quadible Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.quadible.mvp;

import android.support.v4.util.ArrayMap;

import java.util.UUID;

/**
 * <p>
 *     A singleton that is responsible for storing/restoring presenters while the UI elements are
 *     being recreated. So, we can attach the presenters to the new instances of the UI elements.
 *     The presenters are uniquely identified based on a UUID. Also, it is responsible for
 *     restoring the presenters after the app was killed. The action each {@link Presenter} is going
 *     to take on restore is defined in {@link Presenter#onRestore()}.
 * </p>
 */
class PresenterProvider implements IPresenterProvider{

    private ArrayMap<UUID, Presenter> mPresenters = new ArrayMap<>();

    private ArrayMap<UUID, Class> mPresenterTypes = new ArrayMap<>();


    private static PresenterProvider sInstance;

    /**
     * Get the PresenterProvider.
     * @return The PresenterProvider singleton object.
     */
    static PresenterProvider newInstance() {
        if (sInstance == null) {
            sInstance = new PresenterProvider();
        }
        return sInstance;
    }

    /**
     * Store a presenter based on a unique identifier. Later the corresponding ui element can
     * restore the presenter based on this unique identifier.
     * @param uuid The unique identifier.
     * @param presenter The presenter.
     * @param <P> The type of the presenter.
     */
    @Override
    public <P extends Presenter> void add(UUID uuid, P presenter) {
        Check.requireNonNull(uuid);
        Check.requireNonNull(presenter);
        Check.requireNotExist(uuid, mPresenters);

        mPresenters.put(uuid, presenter);
        mPresenterTypes.put(uuid, presenter.getClass());
    }

    /**
     * Get a stored presenter based on the unique identifier.
     * @param uuid The unique identifier.
     * @param <P> The type of the presenter.
     * @return The presenter as <P> type object
     */
    @Override
    public <P extends Presenter> P get(UUID uuid) {
        Check.requireNonNull(uuid);
        Class<P> type = mPresenterTypes.get(uuid);
        return type == null ? null : type.cast(mPresenters.get(uuid));
    }

    /**
     * Remove the {@link Presenter} which corresponds to the given uuid.
     * @param uuid The unique identifier.
     */
    @Override
    public void remove(UUID uuid) {
        Check.requireNonNull(uuid);
        mPresenterTypes.remove(uuid);
        Presenter presenter = mPresenters.get(uuid);

        if (presenter != null) {
            presenter.setRemoved();
            mPresenters.remove(uuid);
        }
    }

}
