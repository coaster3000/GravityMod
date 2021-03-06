/**
 * Copyright 2017 Coaster3000 (Christopher Krier)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package tk.coaster3000.gravity.scheduler;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;
import tk.coaster3000.gravity.IWorldHandle;
import tk.coaster3000.gravity.event.BlockPhysicsEvent;
import tk.coaster3000.gravity.util.PhysicsUtil;

public class PhysicsFellTask extends PhysicsTask implements ValidatedTask {
	/**
	 * Constructs a block felling task that is used in the Physics scheduler to make blocks fall.
	 * @param scheduler used to schedule additional tasks if needed
	 * @param worldHandle involved in the task
	 * @param position within the world involved in the task
	 */
	public PhysicsFellTask(PhysicsScheduler scheduler, IWorldHandle worldHandle, BlockPos position) {
		super(scheduler, worldHandle, position);
	}

	@Override
	public void execute() {
		IBlockState blockState = worldHandle.getBlockState(position);

		BlockPhysicsEvent.Fall bpfEvent = new BlockPhysicsEvent.Fall(worldHandle, position, blockState);

		MinecraftForge.EVENT_BUS.post(bpfEvent);

		if (!(bpfEvent.isCanceled() || bpfEvent.getResult().equals(Event.Result.DENY))) {
			PhysicsUtil.fellBlock(worldHandle, position, blockState);
		}

		//TODO: Cache the task for validation
		//TODO: Cache the result of the fell task to validate on event completion

		markComplete();
	}

	@Override
	public int compareTo(PhysicsTask o) {
		BlockPos oPosition = o.position;

		int diff = Integer.compare(position.getY(), oPosition.getY());

		if (diff != 0) return diff; // check y
		else diff = Integer.compare(position.getX(), oPosition.getY()); // go to X

		if (diff != 0) return diff; // check X
		else return Integer.compare(position.getZ(), oPosition.getZ()); //return Z
	}

	@Override
	public boolean isValid() {
		return false; //FIXME: Check for validity.
	}
}
