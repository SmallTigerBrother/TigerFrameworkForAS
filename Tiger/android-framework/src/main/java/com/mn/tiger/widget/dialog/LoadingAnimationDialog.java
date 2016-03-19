package com.mn.tiger.widget.dialog;

import android.app.Dialog;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mn.tiger.utility.CR;


/**
 * 加载框
 */
public class LoadingAnimationDialog extends TGDialogFragment
{
	private AnimationDrawable animaDrawable;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		int style = DialogFragment.STYLE_NO_TITLE, theme = CR.getStyleId(getActivity(), "LoadingDialogStyle");
		setStyle(style, theme);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(CR.getLayoutId(getActivity(), "tiger_loading_anima_dialog_layout"), container, false);

		ImageView loadingIV = (ImageView) view.findViewById(CR.getViewId(getActivity(), "loading_iv"));

		animaDrawable = (AnimationDrawable) loadingIV.getBackground();
		animaDrawable.start();

		return view;
	}

	@Override
	public void onDestroyView()
	{
		super.onDestroyView();
		animaDrawable.stop();
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		Dialog dialog = super.onCreateDialog(savedInstanceState);
		dialog.setCanceledOnTouchOutside(false);
		return dialog;
	}
}
