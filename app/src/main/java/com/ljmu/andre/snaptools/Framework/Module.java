package com.ljmu.andre.snaptools.Framework;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.ljmu.andre.snaptools.Fragments.FragmentHelper;
import com.ljmu.andre.snaptools.Framework.Utils.LoadState.State;
import com.ljmu.andre.snaptools.Framework.Utils.ModuleLoadState;
import com.ljmu.andre.snaptools.R;
import com.ljmu.andre.snaptools.UIComponents.Adapters.StatefulEListAdapter.StatefulListable;
import com.ljmu.andre.snaptools.Utils.ContextHelper;
import com.ljmu.andre.snaptools.Utils.FrameworkPreferencesDef;

import java.util.List;

import timber.log.Timber;

import static android.view.View.GONE;
import static com.ljmu.andre.snaptools.Utils.FrameworkPreferencesDef.DISABLED_MODULES;
import static com.ljmu.andre.snaptools.Utils.PreferenceHelpers.collectionContains;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public abstract class Module implements StatefulListable<Void> {
	protected ModuleLoadState moduleLoadState;
	private String name;
	private boolean hasInjected = false;
	private boolean canBeDisabled;

	public Module(String name, boolean canBeDisabled) {
		this.name = name;
		this.canBeDisabled = canBeDisabled;
	}

	public abstract FragmentHelper[] getUIFragments();

	/**
	 * ===========================================================================
	 * Performs checks on the Module to ensure it should be loaded or if
	 * {@link this#canBeDisabled()} and {@link FrameworkPreferencesDef#DISABLED_MODULES}
	 * are both true.
	 * <p>
	 * Updates the moduleLoadState to {@link State#FAILED} if any hooks failed, or
	 * {@link State#SUCCESS} if no hooks failed and at least one was successful.
	 * ===========================================================================
	 *
	 * @param moduleLoadState - Provide the Module with a ModuleLoadState from the
	 *                        implementation of this class. Allows us to have more
	 *                        flexibility about the initial state of the Module.
	 * @return The moduleLoadState argument (Unnecessary)
	 */
	@SuppressWarnings({"unused", "WeakerAccess"})

	public ModuleLoadState injectHooks(ClassLoader snapClassLoader, Context snapContext,
									   ModuleLoadState moduleLoadState) {
		if (hasInjected) {
			Timber.d("Tried to reapply hook: %s", name());
			return null;
		}

		hasInjected = true;

		this.moduleLoadState = moduleLoadState;

		if (canBeDisabled()
				&& collectionContains(DISABLED_MODULES, name())) {
			moduleLoadState.setState(State.SKIPPED);
			return moduleLoadState;
		}

		loadHooks(snapClassLoader, snapContext);

		if (moduleLoadState.getFailedHooks() <= 0 &&
				moduleLoadState.getSuccessfulHooks() > 0) {
			moduleLoadState.setState(State.SUCCESS);
		} else if (getModuleLoadState().getFailedHooks() > 0) {
			moduleLoadState.setState(State.FAILED);
		}

		return moduleLoadState;
	}

	public String name() {
		return name;
	}

	protected boolean canBeDisabled() {
		return canBeDisabled;
	}

	/**
	 * ===========================================================================
	 * Signal to the implementation of this class that we wish to load our hooks
	 * into the application. This function should generally not be called unless
	 * we have performed checks on whether the Module is able to hook.
	 * Instead use {@link this#injectHooks(ClassLoader, Context, ModuleLoadState)}
	 * ===========================================================================
	 */
	public abstract void loadHooks(ClassLoader snapClassLoader, Context snapContext);

	/**
	 * ===========================================================================
	 * Additional Hook Point for some modules that require initializing its fields etc with an
	 * activity that cannot be provided at {@link this#loadHooks(ClassLoader, Context)}
	 * ===========================================================================
	 */
	public void prepareActivity(ClassLoader snapClassLoader, Activity snapActivity) {
	}

	public ModuleLoadState getModuleLoadState() {
		return moduleLoadState;
	}

	@Override
	public List<Void> getChildren() {
		return null;
	}

	@Override
	public void updateHeaderText(View container, TextView headerHolder) {
		headerHolder.setText(name());
	}

	@Override
	public void updateMessageText(View container, TextView messageHolder, Void child) {
	}

	@Override
	public void updateHeaderStateHolder(TextView stateHolder) {
		boolean isActive = !collectionContains(DISABLED_MODULES, name());

		if (isActive) {
			stateHolder.setText("Active");
			stateHolder.setBackgroundResource(R.color.success);
			int color = ContextHelper.getModuleResources(stateHolder.getContext()).getColor(R.color.textSecondary);
			stateHolder.setTextColor(color);
		} else {
			stateHolder.setText("Inactive");
			stateHolder.setBackgroundResource(R.color.error);
			int color = ContextHelper.getModuleResources(stateHolder.getContext()).getColor(R.color.textPrimary);
			stateHolder.setTextColor(color);
		}
	}

	@Override
	public void updateMessageStateHolder(TextView stateHolder, Void child) {
		stateHolder.setVisibility(GONE);
	}
}