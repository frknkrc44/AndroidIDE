/*
 *  This file is part of AndroidIDE.
 *
 *  AndroidIDE is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  AndroidIDE is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *   along with AndroidIDE.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.itsaky.androidide.actions.etc

import android.content.Context
import android.view.MenuItem
import androidx.core.content.ContextCompat
import com.itsaky.androidide.R
import com.itsaky.androidide.actions.ActionData
import com.itsaky.androidide.actions.EditorActivityAction
import com.itsaky.androidide.actions.markInvisible
import com.itsaky.androidide.actions.openApplicationModuleChooser
import com.itsaky.androidide.builder.model.UNKNOWN_PACKAGE
import com.itsaky.androidide.projects.IProjectManager
import com.itsaky.androidide.utils.IntentUtils
import com.itsaky.androidide.utils.flashError

/**
 * An action to launch the already installed application on the device.
 *
 * @author Akash Yadav
 */
class LaunchAppAction(context: Context, override val order: Int) : EditorActivityAction() {

  override val id: String = "ide.editor.launchInstalledApp"

  override var requiresUIThread: Boolean = true

  init {
    label = context.getString(R.string.title_launch_app)
    icon = ContextCompat.getDrawable(context, R.drawable.ic_open_external)
  }

  override fun prepare(data: ActionData) {
    data.getActivity() ?: run {
      markInvisible()
      return
    }

    visible = true

    val projectManager = IProjectManager.getInstance()
    enabled = projectManager.getAndroidAppModules().isNotEmpty()
  }

  override fun execAction(data: ActionData) {
    openApplicationModuleChooser(data) { app ->
      val packageName = app.packageName
      if (packageName == UNKNOWN_PACKAGE) {
        flashError(R.string.err_cannot_determine_package)
        return@openApplicationModuleChooser
      }

      val activity = data.requireActivity()
      IntentUtils.launchApp(activity, packageName)
    }
  }

  override fun getShowAsActionFlags(data: ActionData): Int {
    // prefer showing this in the overflow menu
    return MenuItem.SHOW_AS_ACTION_IF_ROOM
  }
}