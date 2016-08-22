package subside.plugins.mcbans.playerapi.Mojang.profiles;

public interface ProfileRepository {
    public Profile[] findProfilesByNames(String... names);
}
