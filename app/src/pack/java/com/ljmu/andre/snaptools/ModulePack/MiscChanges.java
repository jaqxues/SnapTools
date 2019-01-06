package com.ljmu.andre.snaptools.ModulePack;

import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Typeface;
import android.text.InputFilter;
import android.text.InputType;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.SubMenu;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import com.ljmu.andre.snaptools.Dialogs.Content.Options.OptionsButtonData;
import com.ljmu.andre.snaptools.Dialogs.Content.TextInputBasic;
import com.ljmu.andre.snaptools.Dialogs.DialogFactory;
import com.ljmu.andre.snaptools.Dialogs.ThemedDialog;
import com.ljmu.andre.snaptools.Dialogs.ThemedDialog.ThemedClickListener;
import com.ljmu.andre.snaptools.Fragments.FragmentHelper;
import com.ljmu.andre.snaptools.ModulePack.Fragments.ColorPickerExtension;
import com.ljmu.andre.snaptools.ModulePack.Fragments.MiscChangesFragment;
import com.ljmu.andre.snaptools.ModulePack.Utils.FontUtils;
import com.ljmu.andre.snaptools.R;
import com.ljmu.andre.snaptools.Utils.Callable;
import com.ljmu.andre.snaptools.Utils.ContextHelper;
import com.ljmu.andre.snaptools.Utils.ResourceMapper;
import com.ljmu.andre.snaptools.Utils.ResourceUtils;
import com.ljmu.andre.snaptools.Utils.XposedUtils.ST_MethodHook;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

import static com.ljmu.andre.GsonPreferences.Preferences.getPref;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookClassDef.SNAPCHAT_CAPTION_VIEW_CLASS;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookDef.CAPTION_CREATE_HOOK;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookDef.FONT_HOOK;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookVariableDef.SNAPCAPTIONVIEW_CONTEXT;
import static com.ljmu.andre.snaptools.ModulePack.Utils.ModulePreferenceDef.COPY_BUTTON;
import static com.ljmu.andre.snaptools.ModulePack.Utils.ModulePreferenceDef.CURRENT_FONT;
import static com.ljmu.andre.snaptools.ModulePack.Utils.ModulePreferenceDef.CUT_BUTTON;
import static com.ljmu.andre.snaptools.ModulePack.Utils.ModulePreferenceDef.FONTS_PATH;
import static com.ljmu.andre.snaptools.ModulePack.Utils.ModulePreferenceDef.FORCE_MULTILINE;
import static com.ljmu.andre.snaptools.ModulePack.Utils.ModulePreferenceDef.PASTE_BUTTON;

/*import com.flask.colorpicker.ColorPickerView.WHEEL_TYPE;
import com.flask.colorpicker.OnColorChangedListener;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;*/

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class MiscChanges extends ModuleHelper {
	static Boolean fontOverride = true;

	public MiscChanges(String name, boolean canBeDisabled) {
		super(name, canBeDisabled);
	}

	@Override public FragmentHelper[] getUIFragments() {
		return new FragmentHelper[]{new MiscChangesFragment()};
	}

	@Override
	public void loadHooks(ClassLoader snapClassLoader, Context snapContext) {

		hookMethod(FONT_HOOK, new ST_MethodHook() {
			@Override protected void before(MethodHookParam param) throws Throwable {
				if (/*(Boolean)getPref(ENABLE_ST_OVERRIDE_FONT) &&*/ fontOverride) {
					String font = (String) param.args[0];
					String replacement = (String) getPref(FONTS_PATH) + getPref(CURRENT_FONT);
					if (!getPref(CURRENT_FONT).equals("Default") && new File(replacement).exists()) {
						font = replacement;
						Timber.d("[FONT] Font handled by hook");
					}
					param.args[0] = font;
				}
			}
		});

		hookMethod(CAPTION_CREATE_HOOK, new ST_MethodHook() {
			@Override protected void before(MethodHookParam param) throws Throwable {
				/*if (lastUsed == null) {
					Timber.d("No data found from SnapCaptionView. Unable to load context menu.");
					return;
				}*/
				Timber.d("Attempting to load custom menu");
				/*
					Do snapchat's work for them
				 */
				ActionMode actionMode = (ActionMode) param.args[0];
				Menu menu = (Menu) param.args[1];
				menu.clear();
				actionMode.getMenuInflater().inflate(ResourceMapper.getResId(ContextHelper.getActivity(), "caption_context_menu", "menu"), menu);

				EditText text = getObjectField(SNAPCAPTIONVIEW_CONTEXT, param.thisObject);

				int paste = ResourceUtils.getId(ContextHelper.getActivity(), "menu_item_paste");
				int cut = ResourceUtils.getId(ContextHelper.getActivity(), "menu_item_cut");
				int copy = ResourceUtils.getId(ContextHelper.getActivity(), "menu_item_copy");
				/*if (text.getSelectionStart() == text.getSelectionEnd()) {
					menu.findItem(paste).setVisible(true);
					menu.findItem(copy).setVisible(false);
					menu.findItem(cut).setVisible(false);
				} else {
					menu.findItem(copy).setVisible(true);
					menu.findItem(cut).setVisible(true);
					menu.findItem(paste).setVisible(false);
				}*/
				if (getPref(COPY_BUTTON)) {
					menu.findItem(copy).setVisible(true);
				}
				if (getPref(CUT_BUTTON)) {
					menu.findItem(cut).setVisible(true);
				}
				//TODO: check if something is on the clipboard
				ClipboardManager clipboardManager = (ClipboardManager) snapContext.getSystemService(Context.CLIPBOARD_SERVICE);
				if ((Boolean) getPref(PASTE_BUTTON)) {
					if (clipboardManager != null) {
						if (!clipboardManager.hasPrimaryClip()) {
							menu.findItem(paste).setVisible(false);

						} else {
							menu.findItem(paste).setVisible(true);
						}
					} else {
						menu.findItem(paste).setVisible(true);
					}
				}
				int bold = ResourceUtils.getId(ContextHelper.getActivity(), "menu_item_bold");
				int italic = ResourceUtils.getId(ContextHelper.getActivity(), "menu_item_italic");
				int underline = ResourceUtils.getId(ContextHelper.getActivity(), "menu_item_underline");
				menu.findItem(bold).setVisible(true);
				menu.findItem(italic).setVisible(true);
				menu.findItem(underline).setVisible(true);



				/*
					Load our own menuitems, do fancy things
				 */
				MenuItem bgColor = menu.add("BG Color");
				bgColor.setOnMenuItemClickListener(item -> {
					Timber.d("Changing BG color");
					//try {
							/*ColorPickerDialogBuilder
									//TODO: Replace the second snapActivity with a reference to SnapTools' activity
									.with(snapActivity, ContextHelper.getModuleContext(snapActivity))
									.setTitle("Choose BG Color")
									.initialColor(0)
									.wheelType(WHEEL_TYPE.CIRCLE)
									.density(12)
									.setOnColorSelectedListener(i -> {
										//We dont care
									})
									.setPositiveButton("OK", (dialogInterface, selectedColor, integers) -> {
										text.setBackgroundColor(selectedColor);
										dialogInterface.dismiss();
									})
									.setNegativeButton("Cancel", (dialog, which) -> {
										dialog.dismiss(); // just close
									}).build().show();*/

					SubMenu sub = menu.addSubMenu("TestMenu");
					sub.add("Test1");
					sub.add("Test2");
					sub.add("Test3");
					sub.add("Test4");

					//TODO: Attach HSLColorPicker to ThemedDialog
					ThemedDialog prompt = new ThemedDialog(ContextHelper.getActivity());
					Integer color;
					ColorPickerExtension dialog = new ColorPickerExtension(ContextHelper.getActivity());
					dialog.setCallback(new Callable<Integer>() {
						@Override public void call(Integer integer) {
							if (integer == -5 || integer == -1) {
								prompt.dismiss();
							} else {
								Integer color = integer;
								//text.setBackgroundColor(integer);
								prompt.dismiss();
								//Integer opacity = getOpacity(snapActivity);
								text.setBackgroundColor(color);
								//text.setAlpha(Float.parseFloat(opacity/100 + ""));
							}
						}
					});

					prompt.setTitle("Color Picker")
							.setHeaderDrawable(R.drawable.neutral_header)
							.setExtension(dialog)
							.setDismissable(false);
					prompt.show();

					return true;
				});
				bgColor.setVisible(true);

				//if (((Boolean)getPref(ENABLE_ST_OVERRIDE_FONT))) {
				MenuItem font = menu.add("Font");
				font.setOnMenuItemClickListener(new OnMenuItemClickListener() {
					@Override public boolean onMenuItemClick(MenuItem item) {
						ArrayList<OptionsButtonData> buttons = new ArrayList<>();
						List<String> fonts = FontUtils.getInstalledFonts();
						for (String s : fonts) {
							buttons.add(new OptionsButtonData(s, new ThemedClickListener() {
								@Override public void clicked(ThemedDialog themedDialog) {
									if (!s.equalsIgnoreCase("Default")) {
										fontOverride = !fontOverride;
										text.setTypeface(Typeface.createFromFile(new File(getPref(FONTS_PATH) + s)));
										fontOverride = !fontOverride;
									}
									themedDialog.dismiss();
								}
							}));
						}
						OptionsButtonData[] b = buttons.toArray(new OptionsButtonData[buttons.size()]);
						DialogFactory.createOptions(ContextHelper.getActivity(), "Choose Font", b).show();
						return true;
					}
				});
				font.setVisible(true);
				//}
				MenuItem size = menu.add("Size");
				size.setOnMenuItemClickListener(new OnMenuItemClickListener() {
					@Override public boolean onMenuItemClick(MenuItem item) {
						DialogFactory.createBasicTextInputDialog(ContextHelper.getActivity(), "Font Size", "What size should the text be set to?", null, null, InputType.TYPE_CLASS_NUMBER, new ThemedClickListener() {
							@Override public void clicked(ThemedDialog themedDialog) {
								TextInputBasic input = themedDialog.getExtension();
								String inputs = input.getInputMessage();
								try {
									Float f = Float.parseFloat(inputs);
									text.setTextSize(f);
									themedDialog.dismiss();
									return;
								} catch (Exception e) {

								}
								themedDialog.dismiss();
							}
						}).show();
						return true;
					}
				});
				size.setVisible(true);
				param.setResult(true);
			}
		});


		hookAllConstructors(SNAPCHAT_CAPTION_VIEW_CLASS, new ST_MethodHook() {
			@Override protected void after(MethodHookParam param) throws Throwable {
				EditText text = (EditText) param.thisObject;
				if (getPref(FORCE_MULTILINE)) {
					text.setSingleLine(false);
					text.setMaxLines(500);
					text.setImeOptions(EditorInfo.IME_ACTION_NONE);
					text.setFilters(new InputFilter[0]);

					text.setOnLongClickListener(v -> {
						text.showContextMenu();
						return false;
					});
				}
			}
		});
	}

	int getOpacity(Activity snapActivity) {
		final int[] retVal = new int[1];
		DialogFactory.createBasicTextInputDialog(snapActivity, "Opacity", "What should the caption opacity be set to?", "0-100", null, InputType.TYPE_CLASS_NUMBER, new ThemedClickListener() {
			@Override public void clicked(ThemedDialog themedDialog) {
				TextInputBasic input = themedDialog.getExtension();
				String inputs = input.getInputMessage();
				try {
					Integer f = Integer.parseInt(inputs);
					if (f > 100 || f < 0) {
						retVal[0] = -1;
					} else {
						retVal[0] = f;
					}
					themedDialog.dismiss();
					return;
				} catch (Exception e) {

				}
				retVal[0] = -1;
			}
		}).show();
		if (retVal[0] == -1) {
			return getOpacity(snapActivity);
		} else {
			return retVal[0];
		}
	}
}