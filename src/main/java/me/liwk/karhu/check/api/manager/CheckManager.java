package me.liwk.karhu.check.api.manager;

import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.ImmutableClassToInstanceMap;
import lombok.Getter;
import me.liwk.karhu.check.api.Check;
import me.liwk.karhu.check.impl.aimassist.*;
import me.liwk.karhu.check.impl.aimassist.smooth.Mouse;
import me.liwk.karhu.check.impl.aimassist.smooth.Optifine;
import me.liwk.karhu.check.impl.autoclicker.*;
import me.liwk.karhu.check.impl.badpackets.*;
import me.liwk.karhu.check.impl.boatfly.BoatFlyA;
import me.liwk.karhu.check.impl.fastladder.FastLadderA;
import me.liwk.karhu.check.impl.fly.FlyA;
import me.liwk.karhu.check.impl.fly.FlyB;
import me.liwk.karhu.check.impl.hitbox.HitboxA;
import me.liwk.karhu.check.impl.inventory.InventoryA;
import me.liwk.karhu.check.impl.inventory.InventoryB;
import me.liwk.karhu.check.impl.jesus.JesusA;
import me.liwk.karhu.check.impl.killaura.*;
import me.liwk.karhu.check.impl.motion.MotionA;
import me.liwk.karhu.check.impl.motion.MotionB;
import me.liwk.karhu.check.impl.motion.MotionC;
import me.liwk.karhu.check.impl.nofall.NofallA;
import me.liwk.karhu.check.impl.nofall.NofallB;
import me.liwk.karhu.check.impl.noslow.NoSlowA;
import me.liwk.karhu.check.impl.noslow.NoSlowB;
import me.liwk.karhu.check.impl.pingspoof.PingSpoofA;
import me.liwk.karhu.check.impl.pingspoof.PingSpoofB;
import me.liwk.karhu.check.impl.reach.ReachA;
import me.liwk.karhu.check.impl.reach.ReachB;
import me.liwk.karhu.check.impl.scaffold.ScaffoldA;
import me.liwk.karhu.check.impl.speed.SpeedA;
import me.liwk.karhu.check.impl.speed.SpeedB;
import me.liwk.karhu.check.impl.speed.SpeedC;
import me.liwk.karhu.check.impl.sprint.OmniSprintA;
import me.liwk.karhu.check.impl.step.StepA;
import me.liwk.karhu.check.impl.step.StepB;
import me.liwk.karhu.check.impl.timer.TimerA;
import me.liwk.karhu.check.impl.timer.TimerB;
import me.liwk.karhu.check.impl.velocity.VelocityA;
import me.liwk.karhu.check.impl.velocity.VelocityB;
import me.liwk.karhu.data.PlayerData;

import java.util.Collection;

//import me.liwk.karhu.check.impl.hitbox.HitboxA;

@Getter
public final class CheckManager {
    private final ClassToInstanceMap<Check> checks;

    public CheckManager(final PlayerData playerData) {
        checks = new ImmutableClassToInstanceMap.Builder<Check>()

                .put(KillauraA.class, new KillauraA(playerData))
                .put(KillauraB.class, new KillauraB(playerData))
                .put(KillauraC.class, new KillauraC(playerData))
                .put(KillauraD.class, new KillauraD(playerData))
                .put(KillauraE.class, new KillauraE(playerData))
                .put(KillauraF.class, new KillauraF(playerData))
                .put(KillauraG.class, new KillauraG(playerData))
                .put(KillauraH.class, new KillauraH(playerData))
                .put(KillauraI.class, new KillauraI(playerData))
                .put(KillauraJ.class, new KillauraJ(playerData))

                .put(HitboxA.class, new HitboxA(playerData))

                .put(ReachA.class, new ReachA(playerData))
                .put(ReachB.class, new ReachB(playerData))

                .put(VelocityA.class, new VelocityA(playerData))
                .put(VelocityB.class, new VelocityB(playerData))

                .put(Optifine.class, new Optifine(playerData))
                .put(Mouse.class, new Mouse(playerData))

                .put(AimA.class, new AimA(playerData))
                .put(AimB.class, new AimB(playerData))
                .put(AimC.class, new AimC(playerData))
                .put(AimD.class, new AimD(playerData))
                .put(AimE.class, new AimE(playerData))
                .put(AimF.class, new AimF(playerData))
                .put(AimG.class, new AimG(playerData))
                .put(AimH.class, new AimH(playerData))
                .put(AimI.class, new AimI(playerData))
                .put(AimJ.class, new AimJ(playerData))
                .put(AimK.class, new AimK(playerData))
                .put(AimL.class, new AimL(playerData))
                .put(AimM.class, new AimM(playerData))
                .put(AimO.class, new AimO(playerData))

                .put(BadA.class, new BadA(playerData))
                .put(BadB.class, new BadB(playerData))
                .put(BadC.class, new BadC(playerData))
                .put(BadD.class, new BadD(playerData))
                .put(BadE.class, new BadE(playerData))

                .put(FlyA.class, new FlyA(playerData))
                .put(FlyB.class, new FlyB(playerData))

                .put(BoatFlyA.class, new BoatFlyA(playerData))

                .put(StepA.class, new StepA(playerData))
                .put(StepB.class, new StepB(playerData))

                //.put(PhaseA.class, new PhaseA(playerData))

                .put(AutoclickerA.class, new AutoclickerA(playerData))
                .put(AutoclickerB.class, new AutoclickerB(playerData))
                .put(AutoclickerC.class, new AutoclickerC(playerData))
                .put(AutoclickerD.class, new AutoclickerD(playerData))
                .put(AutoclickerE.class, new AutoclickerE(playerData))
                .put(AutoclickerF.class, new AutoclickerF(playerData))

                .put(TimerA.class, new TimerA(playerData))
                .put(TimerB.class, new TimerB(playerData))

                .put(SpeedA.class, new SpeedA(playerData))
                .put(SpeedB.class, new SpeedB(playerData))
                .put(SpeedC.class, new SpeedC(playerData))

                .put(NoSlowA.class, new NoSlowA(playerData))
                .put(NoSlowB.class, new NoSlowB(playerData))

                .put(MotionA.class, new MotionA(playerData))
                .put(MotionB.class, new MotionB(playerData))
                .put(MotionC.class, new MotionC(playerData))

                .put(InventoryA.class, new InventoryA(playerData))
                .put(InventoryB.class, new InventoryB(playerData))

                .put(FastLadderA.class, new FastLadderA(playerData))

                .put(ScaffoldA.class, new ScaffoldA(playerData))

                .put(NofallA.class, new NofallA(playerData))
                .put(NofallB.class, new NofallB(playerData))

                .put(OmniSprintA.class, new OmniSprintA(playerData))

                .put(JesusA.class, new JesusA(playerData))

                .put(PingSpoofA.class, new PingSpoofA(playerData))
                .put(PingSpoofB.class, new PingSpoofB(playerData))
                .build();
    }

    public Collection<Check> getChecks() {
        if(checks != null)
            return checks.values();
        return null;
    }

    public int checkAmount() {
        if(this.getChecks() != null)
            return this.getChecks().size();
        return 0;
    }

    public Check getCheck(final Class<? extends Check> clazz) {
        return checks.getInstance(clazz);
    }


}
